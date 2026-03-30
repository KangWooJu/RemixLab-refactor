package org.woojukang.remixlab.domain.video.entity;


import jakarta.persistence.*;
import lombok.*;
import org.woojukang.remixlab.domain.creation.entity.Creation;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Creation creation;

    private String soraVideoId;

    private String url;

    private String videoStatus;

    public void updateStatus(String status) {
        this.videoStatus = status;
    }

    public void updateUrl(String url) {
        this.url = url;
    }
}
