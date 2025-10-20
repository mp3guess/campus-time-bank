-- TODO: Implement bookings table creation

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    offer_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reserved_hours NUMERIC(10, 2) NOT NULL,
    transferred_hours NUMERIC(10, 2),
    cancel_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    completed_at TIMESTAMP,
    canceled_at TIMESTAMP,
    CONSTRAINT fk_booking_offer FOREIGN KEY (offer_id) REFERENCES offers(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_requester FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_bookings_offer_id ON bookings(offer_id);
CREATE INDEX idx_bookings_requester_id ON bookings(requester_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_created_at ON bookings(created_at);
