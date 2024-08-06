package com.cristian.shop.Model;

import com.cristian.shop.enum_.StatusOrder;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private Boolean orderActive;
    private Boolean orderFinish;

    @Enumerated(EnumType.STRING)
    private StatusOrder status;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalTime createdAt;

    @UpdateTimestamp
    private LocalTime updatedAt;
    
    @ManyToOne()
    @JoinColumn(name = "user")
    private User user;

}
