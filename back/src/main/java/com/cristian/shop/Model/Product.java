package com.cristian.shop.Model;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String category;
    private String amount;
    private String price;
    private String waist;

    // add service save img.* in back
    private String img;

    @ManyToOne()
    @JoinColumn(name = "admin_id")
    private User admin;
}
