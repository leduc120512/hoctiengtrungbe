package com.example.bewebtiengtrung.module.sentence.repository;

import com.example.bewebtiengtrung.module.sentence.entity.SentenceSource;
import com.example.bewebtiengtrung.module.sentence.entity.UserSentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Truy cập bảng {@code user_sentences}. */
@Repository
public interface UserSentenceRepository extends JpaRepository<UserSentence, Long> {

    /** Toàn bộ câu của một người dùng, sắp theo cấp rồi theo id (thứ tự thêm vào). */
    List<UserSentence> findByUserIdOrderByLevelAscIdAsc(Long userId);

    /** Mọi {@code hanzi_key} của người dùng — dùng để chống trùng mà không nạp cả entity. */
    @Query("select s.hanziKey from UserSentence s where s.userId = :userId")
    List<String> findHanziKeysByUserId(@Param("userId") Long userId);

    /** Mọi câu (chỉ chữ Hán) của người dùng — gửi kèm cho AI với yêu cầu "đừng tạo lại". */
    @Query("select s.hanzi from UserSentence s where s.userId = :userId")
    List<String> findHanziByUserId(@Param("userId") Long userId);

    /** Xoá hàng loạt theo nguồn; trả về số dòng đã xoá. */
    @Modifying
    @Query("delete from UserSentence s where s.userId = :userId and s.source = :source")
    int deleteByUserIdAndSource(@Param("userId") Long userId, @Param("source") SentenceSource source);
}
