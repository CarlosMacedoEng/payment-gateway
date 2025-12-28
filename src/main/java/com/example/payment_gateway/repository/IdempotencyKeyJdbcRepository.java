package com.example.payment_gateway.repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.payment_gateway.domain.IdempotencyKey;

import lombok.RequiredArgsConstructor;

// JDBC-based repository responsible for persisting and retrieving idempotency keys.
@Repository
@RequiredArgsConstructor
public class IdempotencyKeyJdbcRepository implements IdempotencyKeyRepository {

    // Provides low-level access to the database using SQL.
    private final JdbcTemplate jdbcTemplate;

    // Retrieves an idempotency key record if it already exists.
    @Override
    public Optional<IdempotencyKey> findByKey(String idempotencyKey) {
        String sql = """
            SELECT idempotency_key, request_hash, payment_id, created_at
            FROM idempotency_keys
            WHERE idempotency_key = ?
        """;

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                                    IdempotencyKey.builder()
                                            .idempotencyKey(rs.getString("idempotency_key"))
                                            .requestHash(rs.getString("request_hash"))
                                            .paymentId((UUID) rs.getObject("payment_id"))
                                            .createdAt(rs.getTimestamp("created_at").toInstant())
                                            .build(),
                            idempotencyKey
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            // Returns empty when no record is found for the given key.
            return Optional.empty();
        }
    }

    // Inserts a new idempotency key or returns the existing one in case of conflict.
    @Override
    public IdempotencyKey insertOrGetExisting(String idempotencyKey, String requestHash, UUID paymentId) {

        // Uses ON CONFLICT DO NOTHING to ensure atomic idempotency handling at the database level.
        // If insertion fails, the existing record must be retrieved separately.
        String insertSql = """
            INSERT INTO idempotency_keys (idempotency_key, request_hash, payment_id, created_at)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (idempotency_key) DO NOTHING
            RETURNING idempotency_key, request_hash, payment_id, created_at
        """;

        Instant now = Instant.now();

        try {
            return jdbcTemplate.queryForObject(
                    insertSql,
                    (rs, rowNum) -> IdempotencyKey.builder()
                            .idempotencyKey(rs.getString("idempotency_key"))
                            .requestHash(rs.getString("request_hash"))
                            .paymentId((UUID) rs.getObject("payment_id"))
                            .createdAt(rs.getTimestamp("created_at").toInstant())
                            .build(),
                    idempotencyKey,
                    requestHash,
                    paymentId,
                    Timestamp.from(now)
            );
        } catch (EmptyResultDataAccessException ignored) {
            // Handles concurrent insert conflicts by loading the already persisted record.
            return findByKey(idempotencyKey)
                    .orElseThrow(() -> new IllegalStateException(
                            "Idempotency key conflict detected but existing record was not found. Key=" + idempotencyKey
                    ));
        }
    }
}
