package com.breaze.genesis.entity.tokens;

import com.breaze.genesis.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_wallet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenWallet {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tokens_available", nullable = false)
    private Integer tokensAvailable;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Integer version;

    @PrePersist
    public void prePersist() {
        if (this.tokensAvailable == null) {
            this.tokensAvailable = 0;
        }
        if (this.tokensAvailable < 0) {
            throw new IllegalStateException("Wallet balance cannot be negative");
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.version == null) {
            this.version = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.tokensAvailable == null || this.tokensAvailable < 0) {
            throw new IllegalStateException("Wallet balance cannot be negative");
        }
        this.updatedAt = LocalDateTime.now();
    }
}
