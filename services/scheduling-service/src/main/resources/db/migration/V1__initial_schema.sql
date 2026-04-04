-- hobsinn V1 Initial Schema
-- Managed by Flyway. Never edit this file after it has been applied.
-- To modify schema: create V2__description.sql

CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ─────────────────────────────────────────────────────────────────────
-- USERS
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE users (
    user_id            UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone_number       VARCHAR(20)  NOT NULL UNIQUE,
    password_hash      VARCHAR(255) NOT NULL,
    full_name          VARCHAR(150) NOT NULL,
    role               VARCHAR(30)  NOT NULL
                       CHECK (role IN ('HOUSEHOLD','PICKUP','AMBASSADOR','ADMINISTRATOR')),
    preferred_language VARCHAR(5)   NOT NULL DEFAULT 'en'
                       CHECK (preferred_language IN ('en','fr')),
    badge_count        INTEGER      NOT NULL DEFAULT 0,
    rating_score       DECIMAL(5,4) NOT NULL DEFAULT 1.0
                       CHECK (rating_score BETWEEN 0.0 AND 1.0),
    star_rating        DECIMAL(3,2) NOT NULL DEFAULT 6.0
                       CHECK (star_rating BETWEEN 0.0 AND 6.0),
    is_active          BOOLEAN      NOT NULL DEFAULT true,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_users_phone  ON users(phone_number);
CREATE INDEX idx_users_role   ON users(role);

-- ─────────────────────────────────────────────────────────────────────
-- PROVIDERS
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE providers (
    provider_id       UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id           UUID          NOT NULL REFERENCES users(user_id),
    company_name      VARCHAR(150)  NOT NULL,
    vehicle_type      VARCHAR(20)   NOT NULL
                      CHECK (vehicle_type IN ('WAGON','BIKE','TRICYCLE','TRUCK')),
    capacity_kg       DECIMAL(10,2) NOT NULL CHECK (capacity_kg > 0),
    service_zone      GEOGRAPHY(POLYGON,4326),
    reliability_score DECIMAL(5,4)  NOT NULL DEFAULT 1.0
                      CHECK (reliability_score BETWEEN 0.0 AND 1.0),
    is_verified       BOOLEAN       NOT NULL DEFAULT false,
    is_active         BOOLEAN       NOT NULL DEFAULT true,
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_providers_user      ON providers(user_id);
CREATE INDEX idx_providers_zone      ON providers USING GIST(service_zone);
CREATE INDEX idx_providers_verified  ON providers(is_verified) WHERE is_verified = true;

-- ─────────────────────────────────────────────────────────────────────
-- PROVIDER SCHEDULE SLOTS (General Pickup)
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE provider_schedule_slots (
    slot_id       UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    provider_id   UUID        NOT NULL REFERENCES providers(provider_id),
    community_id  UUID,
    day_of_week   INTEGER     NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time    TIME        NOT NULL,
    end_time      TIME        NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'FREE'
                  CHECK (status IN ('FREE','BOOKED','BLOCKED')),
    valid_until   DATE        NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CHECK (end_time > start_time)
);
CREATE INDEX idx_slots_provider ON provider_schedule_slots(provider_id);
CREATE INDEX idx_slots_free     ON provider_schedule_slots(day_of_week, status)
    WHERE status = 'FREE';

-- ─────────────────────────────────────────────────────────────────────
-- PICKUP REQUESTS (Special Call + General)
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE pickup_requests (
    request_id           UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    requesting_user_id   UUID          NOT NULL REFERENCES users(user_id),
    assigned_provider_id UUID          REFERENCES providers(provider_id),
    request_type         VARCHAR(20)   NOT NULL DEFAULT 'SPECIAL_CALL'
                         CHECK (request_type IN ('SPECIAL_CALL','GENERAL')),
    location_lat         DECIMAL(10,7) NOT NULL,
    location_lng         DECIMAL(10,7) NOT NULL,
    location_point       GEOGRAPHY(POINT,4326),
    bag_count            INTEGER       NOT NULL CHECK (bag_count > 0),
    estimated_volume_m3  DECIMAL(8,3),
    estimated_cost_xaf   DECIMAL(12,2) NOT NULL,
    platform_fee_xaf     DECIMAL(12,2) NOT NULL,
    total_cost_xaf       DECIMAL(12,2) NOT NULL,
    requested_time       TIMESTAMPTZ   NOT NULL,
    status               VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                         CHECK (status IN (
                             'PENDING','INVALID','MATCHED','CONFIRMED',
                             'IN_PROGRESS','COMPLETED','CANCELLED','REASSIGNED'
                         )),
    idempotency_key      VARCHAR(255)  NOT NULL UNIQUE,
    created_at           TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CHECK (requested_time > created_at)
);
CREATE INDEX idx_requests_user     ON pickup_requests(requesting_user_id);
CREATE INDEX idx_requests_provider ON pickup_requests(assigned_provider_id)
    WHERE assigned_provider_id IS NOT NULL;
CREATE INDEX idx_requests_active   ON pickup_requests(status, requested_time)
    WHERE status NOT IN ('COMPLETED','CANCELLED','INVALID');

-- ─────────────────────────────────────────────────────────────────────
-- JOB SCORES
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE job_scores (
    score_id              UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    request_id            UUID         NOT NULL UNIQUE REFERENCES pickup_requests(request_id),
    pickup_user_id        UUID         NOT NULL REFERENCES users(user_id),
    punctuality_pct       DECIMAL(5,2) NOT NULL CHECK (punctuality_pct BETWEEN 0 AND 100),
    completion_pct        DECIMAL(5,2) NOT NULL CHECK (completion_pct BETWEEN 0 AND 100),
    customer_rating_stars INTEGER      CHECK (customer_rating_stars BETWEEN 1 AND 6),
    composite_score_pct   DECIMAL(5,2) NOT NULL CHECK (composite_score_pct BETWEEN 0 AND 100),
    recorded_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_scores_pickup_user ON job_scores(pickup_user_id);

-- ─────────────────────────────────────────────────────────────────────
-- TRANSACTIONS
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE transactions (
    transaction_id          UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    source_type             VARCHAR(30)   NOT NULL
                            CHECK (source_type IN ('pickup_request','campaign_contribution')),
    source_id               UUID          NOT NULL,
    amount_xaf              DECIMAL(12,2) NOT NULL CHECK (amount_xaf > 0),
    payer_msisdn            VARCHAR(20)   NOT NULL,
    status                  VARCHAR(20)   NOT NULL DEFAULT 'INITIATED'
                            CHECK (status IN (
                                'INITIATED','PENDING','CONFIRMED',
                                'RELEASED','REFUNDED','FAILED'
                            )),
    idempotency_key         VARCHAR(255)  NOT NULL UNIQUE,
    gateway_transaction_id  VARCHAR(255)  UNIQUE,
    initiated_at            TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    confirmed_at            TIMESTAMPTZ,
    released_at             TIMESTAMPTZ
);
CREATE INDEX idx_transactions_source  ON transactions(source_type, source_id);
CREATE INDEX idx_transactions_pending ON transactions(status, initiated_at)
    WHERE status IN ('INITIATED','PENDING');

-- ─────────────────────────────────────────────────────────────────────
-- DUMP REPORTS
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE dump_reports (
    report_id            UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    reporter_id          UUID          NOT NULL REFERENCES users(user_id),
    ambassador_id        UUID          REFERENCES users(user_id),
    location_lat         DECIMAL(10,7) NOT NULL,
    location_lng         DECIMAL(10,7) NOT NULL,
    location_point       GEOGRAPHY(POINT,4326),
    description          TEXT,
    photo_url            VARCHAR(500),
    ambassador_photo_url VARCHAR(500),
    estimated_volume_m3  DECIMAL(8,3),
    cleanup_cost_xaf     DECIMAL(12,2),
    status               VARCHAR(20)   NOT NULL DEFAULT 'SUBMITTED'
                         CHECK (status IN (
                             'SUBMITTED','PENDING','CONFIGURED',
                             'FUNDED','IN_PROGRESS','COMPLETED',
                             'REJECTED','CANCELLED'
                         )),
    rejection_reason     TEXT,
    created_at           TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    validated_at         TIMESTAMPTZ,
    updated_at           TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_dump_reports_reporter    ON dump_reports(reporter_id);
CREATE INDEX idx_dump_reports_ambassador  ON dump_reports(ambassador_id)
    WHERE ambassador_id IS NOT NULL;
CREATE INDEX idx_dump_reports_location    ON dump_reports USING GIST(location_point);
CREATE INDEX idx_dump_reports_active      ON dump_reports(status)
    WHERE status NOT IN ('COMPLETED','REJECTED','CANCELLED');

-- ─────────────────────────────────────────────────────────────────────
-- INCIDENT REPORTS (Dirty Environment)
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE incident_reports (
    incident_id       UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    reporter_id       UUID          NOT NULL REFERENCES users(user_id),
    ambassador_id     UUID          REFERENCES users(user_id),
    location_lat      DECIMAL(10,7) NOT NULL,
    location_lng      DECIMAL(10,7) NOT NULL,
    location_point    GEOGRAPHY(POINT,4326),
    description       TEXT,
    photo_url         VARCHAR(500),
    severity_level    VARCHAR(20)   NOT NULL
                      CHECK (severity_level IN (
                          'AVERAGE','DIRTY','DIRTY_LV1','DIRTY_LV2','DIRTY_LV3'
                      )),
    estimated_area_m2 DECIMAL(10,2),
    status            VARCHAR(20)   NOT NULL DEFAULT 'SUBMITTED'
                      CHECK (status IN (
                          'SUBMITTED','OPEN','CLOSED','IN_SESSION',
                          'IN_PROGRESS','COMPLETED'
                      )),
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    validated_at      TIMESTAMPTZ,
    updated_at        TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_incident_location ON incident_reports USING GIST(location_point);
CREATE INDEX idx_incident_active   ON incident_reports(status, created_at)
    WHERE status NOT IN ('COMPLETED');

-- ─────────────────────────────────────────────────────────────────────
-- CAMPAIGNS
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE campaigns (
    campaign_id          UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    linked_report_id     UUID          UNIQUE,
    title                VARCHAR(250)  NOT NULL,
    description          TEXT,
    campaign_type        VARCHAR(30)   NOT NULL
                         CHECK (campaign_type IN (
                             'DUMP_CLEANUP','DIRTY_CLEANUP',
                             'SENSITIZATION','HEALTH'
                         )),
    funding_target_xaf   DECIMAL(12,2),
    current_amount_raised DECIMAL(12,2) NOT NULL DEFAULT 0,
    contributor_count    INTEGER       NOT NULL DEFAULT 0,
    volunteer_count      INTEGER       NOT NULL DEFAULT 0,
    deadline             TIMESTAMPTZ,
    status               VARCHAR(20)   NOT NULL DEFAULT 'SUBMITTED'
                         CHECK (status IN (
                             'SUBMITTED','PENDING','OPEN','CLOSED','CONFIGURED',
                             'FUNDED','IN_SESSION','IN_PROGRESS','COMPLETED',
                             'CANCELLED'
                         )),
    assigned_ambassador_id UUID        REFERENCES users(user_id),
    completion_photo_url VARCHAR(500),
    celebration_text     TEXT,
    created_at           TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_campaigns_type    ON campaigns(campaign_type, status);
CREATE INDEX idx_campaigns_active  ON campaigns(status)
    WHERE status NOT IN ('COMPLETED','CANCELLED');

-- ─────────────────────────────────────────────────────────────────────
-- CAMPAIGN CONTRIBUTIONS
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE campaign_contributions (
    contribution_id UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    campaign_id     UUID          NOT NULL REFERENCES campaigns(campaign_id),
    contributor_id  UUID          NOT NULL REFERENCES users(user_id),
    amount_xaf      DECIMAL(12,2) NOT NULL CHECK (amount_xaf > 0),
    transaction_id  UUID          REFERENCES transactions(transaction_id),
    idempotency_key VARCHAR(255)  NOT NULL UNIQUE,
    contributed_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_contributions_campaign     ON campaign_contributions(campaign_id);
CREATE INDEX idx_contributions_contributor  ON campaign_contributions(contributor_id);

-- ─────────────────────────────────────────────────────────────────────
-- CAMPAIGN VOLUNTEERS
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE campaign_volunteers (
    volunteer_id       UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    campaign_id        UUID        NOT NULL REFERENCES campaigns(campaign_id),
    user_id            UUID        NOT NULL REFERENCES users(user_id),
    full_name          VARCHAR(150) NOT NULL,
    volunteer_program  VARCHAR(30) NOT NULL
                       CHECK (volunteer_program IN ('CLEANUP','SENSITIZATION','HEALTH')),
    committed_date     TIMESTAMPTZ NOT NULL,
    attendance_status  VARCHAR(20) NOT NULL DEFAULT 'REGISTERED'
                       CHECK (attendance_status IN ('REGISTERED','ATTENDED','ABSENT')),
    certificate_issued BOOLEAN     NOT NULL DEFAULT false,
    registered_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (campaign_id, user_id)
);
CREATE INDEX idx_volunteers_campaign ON campaign_volunteers(campaign_id);
CREATE INDEX idx_volunteers_attended ON campaign_volunteers(campaign_id, attendance_status)
    WHERE attendance_status = 'ATTENDED';

-- ─────────────────────────────────────────────────────────────────────
-- OUTBOX EVENTS (Guaranteed Kafka delivery)
-- ─────────────────────────────────────────────────────────────────────
CREATE TABLE outbox_events (
    outbox_id    UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_type   VARCHAR(100) NOT NULL,
    kafka_topic  VARCHAR(100) NOT NULL,
    payload      JSONB        NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                 CHECK (status IN ('PENDING','PUBLISHED','FAILED')),
    retry_count  INTEGER      NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    published_at TIMESTAMPTZ
);
CREATE INDEX idx_outbox_pending ON outbox_events(created_at)
    WHERE status = 'PENDING';
