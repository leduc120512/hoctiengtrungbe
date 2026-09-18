package com.example.bewebtiengtrung.module.vocabulary.entity;

import com.example.bewebtiengtrung.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Từ vựng tiếng Trung — ánh xạ bảng {@code words}.
 *
 * <p>Word sở hữu quan hệ 1-n tới {@link WordExample} (cascade ALL + orphanRemoval)
 * và là phía CHỦ SỞ HỮU (owning side) của quan hệ n-n với {@link Topic}
 * thông qua bảng trung gian {@code word_topics}.</p>
 */
@Entity
@Table(
        name = "words",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_words_simplified_pinyin",
                columnNames = {"simplified", "pinyin"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Word extends BaseEntity {

    /** Chữ Hán giản thể. */
    @Column(name = "simplified", nullable = false, length = 60)
    private String simplified;

    /** Chữ Hán phồn thể. */
    @Column(name = "traditional", length = 60)
    private String traditional;

    /** Phiên âm pinyin có dấu thanh (ví dụ: nǐ hǎo). */
    @Column(name = "pinyin", nullable = false, length = 120)
    private String pinyin;

    /** Phiên âm pinyin đánh số thanh điệu (ví dụ: ni3 hao3). */
    @Column(name = "pinyin_numbered", length = 120)
    private String pinyinNumbered;

    /** Nghĩa tiếng Việt. */
    @Column(name = "meaning_vi", nullable = false, length = 500)
    private String meaningVi;

    /** Nghĩa tiếng Anh (tham khảo). */
    @Column(name = "meaning_en", length = 500)
    private String meaningEn;

    /** Từ loại: danh từ, động từ, tính từ... */
    @Column(name = "part_of_speech", length = 30)
    private String partOfSpeech;

    /** Cấp độ HSK (1..9). */
    @Builder.Default
    @Column(name = "hsk_level", nullable = false)
    private Integer hskLevel = 1;

    /** Số nét của chữ. */
    @Column(name = "stroke_count")
    private Integer strokeCount;

    /** Thứ hạng theo tần suất sử dụng. */
    @Column(name = "frequency_rank")
    private Integer frequencyRank;

    /** Đường dẫn file audio phát âm của từ. */
    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    /** Đường dẫn ảnh minh hoạ. */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /** Ghi chú thêm dành cho người học. */
    @Column(name = "note", length = 1000)
    private String note;

    /**
     * Danh sách câu ví dụ — là con thực sự của Word nên cascade ALL + orphanRemoval.
     *
     * <p>Bắt buộc dùng {@link Set} chứ KHÔNG dùng {@code List}: truy vấn chi tiết
     * (xem {@code WordRepository#findDetailById}) nạp đồng thời hai tập {@code examples}
     * và {@code topics} bằng một @EntityGraph, sinh tích Descartes ở tầng SQL. Nếu đây là
     * bag (List) thì mỗi câu ví dụ sẽ bị lặp lại đúng bằng số chủ đề của từ — đã kiểm chứng
     * thực tế: 2 câu ví dụ x 3 chủ đề trả về 6 phần tử. Set khử trùng lặp nên luôn đúng.
     * Thứ tự hiển thị do {@code WordMapper#toDetail} sắp xếp lại.</p>
     */
    @Builder.Default
    @OneToMany(
            mappedBy = "word",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("sortOrder ASC, id ASC")
    private Set<WordExample> examples = new LinkedHashSet<>();

    /** Các chủ đề chứa từ này — Word là phía chủ sở hữu của bảng {@code word_topics}. */
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "word_topics",
            joinColumns = @JoinColumn(name = "word_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private Set<Topic> topics = new LinkedHashSet<>();

    /** Thêm một câu ví dụ và đồng bộ quan hệ hai chiều. */
    public void addExample(WordExample example) {
        example.setWord(this);
        this.examples.add(example);
    }

    /** Gỡ một câu ví dụ — orphanRemoval sẽ xoá bản ghi tương ứng trong DB. */
    public void removeExample(WordExample example) {
        this.examples.remove(example);
        example.setWord(null);
    }

    /** Xoá toàn bộ câu ví dụ hiện có (dùng khi cập nhật toàn phần một từ). */
    public void clearExamples() {
        for (WordExample example : new ArrayList<>(this.examples)) {
            removeExample(example);
        }
    }

    /** Gán lại tập chủ đề cho từ vựng. */
    public void replaceTopics(Set<Topic> newTopics) {
        this.topics.clear();
        if (newTopics != null) {
            this.topics.addAll(newTopics);
        }
    }
}
