package edu.cit.yungco.expensemini.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    /* == SDD REQUIRED COLUMNS == */
    @Column(name = "category_id", nullable = false)
    private Long dbCategoryId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "notes")
    private String notes;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "is_recurring")
    private Boolean isRecurring;

    @Column(name = "recurring_type")
    private String recurringType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Transient logic handlers
    @Column(name = "category", nullable = false)
    @Builder.Default
    private String category = "Synced";

    /*
     * == SAFE-GUARDING OBSOLETE DB LOGIC ==
     * Fills the old Phase 1 Supabase NOT NULL 'date' and 'description' columns to
     * prevent crash without manual intervention
     */
    @Column(name = "description", nullable = false)
    @Builder.Default
    private String description = "Synced";

    @Column(name = "date", nullable = false)
    @Builder.Default
    private LocalDate legacyDate = LocalDate.now();

    @Column(name = "receipt_url")
    private String receiptUrl;

    public String getDescription() {
        return this.title != null ? this.title : "Expense";
    }

    public String getCategoryString() {
        if (dbCategoryId == null)
            return "Other";
        if (dbCategoryId == 1L)
            return "Food";
        if (dbCategoryId == 2L)
            return "Transport";
        if (dbCategoryId == 3L)
            return "School";
        if (dbCategoryId == 4L)
            return "Personal";
        return "Other";
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null)
            this.createdAt = LocalDateTime.now();
        if (this.legacyDate == null)
            this.legacyDate = LocalDate.now();
        if (this.description == null)
            this.description = "Synced";
        if (this.category == null)
            this.category = "Synced";
        if (this.isRecurring == null)
            this.isRecurring = false;
    }
}
