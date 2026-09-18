package com.example.bewebtiengtrung.module.srs.entity;

import com.example.bewebtiengtrung.common.entity.BaseEntity;
import com.example.bewebtiengtrung.module.user.entity.User;
import com.example.bewebtiengtrung.module.vocabulary.entity.Topic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Bộ thẻ (deck) chứa các flashcard.
 *
 * <p>Quy tắc hiển thị: một deck được đọc khi {@code isPublic} hoặc {@code isSystem} là true,
 * hoặc người dùng hiện tại chính là chủ sở hữu. Deck hệ thống ({@code isSystem}) có
 * {@code owner} bằng null.</p>
 *
 * <p>Ánh xạ bảng {@code decks}.</p>
 */
@Entity
@Table(
        name = "decks",
        uniqueConstraints = @UniqueConstraint(name = "uk_decks_slug", columnNames = "slug")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deck extends BaseEntity {

    /** Chủ sở hữu deck – null với deck hệ thống. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    /**
     * Bản sao chỉ-đọc của khoá ngoại {@code owner_id}.
     *
     * <p>Nhờ trường này, việc kiểm tra quyền và dựng DTO chỉ cần đọc id mà không phải
     * nạp entity {@link User} – vốn có {@code roles} fetch EAGER nên sẽ sinh thêm một
     * truy vấn {@code user_roles} cho mỗi deck (lỗi N+1) khi liệt kê danh sách.</p>
     *
     * <p>Cột đã được ánh xạ bởi {@link #owner} nên ở đây phải để
     * {@code insertable = false, updatable = false}; khi gán {@link #owner} nhớ gán kèm
     * trường này để đối tượng trong bộ nhớ luôn nhất quán.</p>
     */
    @Column(name = "owner_id", insertable = false, updatable = false)
    private Long ownerId;

    /** Chủ đề từ vựng gắn với deck (tuỳ chọn). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    /** Bản sao chỉ-đọc của khoá ngoại {@code topic_id} – xem giải thích ở {@link #ownerId}. */
    @Column(name = "topic_id", insertable = false, updatable = false)
    private Long topicId;

    /** Định danh thân thiện URL – có thể null, nhưng nếu có thì phải duy nhất. */
    @Column(name = "slug", length = 150)
    private String slug;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    /** Cấp độ HSK gợi ý của deck (1..6), có thể null. */
    @Column(name = "hsk_level")
    private Integer hskLevel;

    /** Cho phép mọi người dùng khác xem deck này. */
    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = Boolean.FALSE;

    /** Deck do hệ thống tạo sẵn – luôn hiển thị cho mọi người, chỉ ADMIN được sửa. */
    @Builder.Default
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = Boolean.FALSE;

    /** Số thẻ hiện có – luôn được đồng bộ khi thêm/xoá flashcard. */
    @Builder.Default
    @Column(name = "card_count", nullable = false)
    private Integer cardCount = 0;

    /** Danh sách thẻ thuộc deck – là con thực sự nên cascade ALL + orphanRemoval. */
    @Builder.Default
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flashcard> flashcards = new ArrayList<>();
}
