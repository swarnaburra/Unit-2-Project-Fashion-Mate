package fashionmate_backend.models;

import jakarta.persistence.*;
import org.springframework.aot.generate.GeneratedTypeReference;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
public class StyleLens {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    //UserId is a foreign key to the User table,
    // it is used to link the StyleLens to a specific user

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String decision;
    @Lob
    private String image;

    public StyleLens(){

    }

    //Getters and Setters

    public Long getId(){
        return id;
    }


    public void setId(Long id){
        this.id = id;
    }

    public String getDecision(){
        return decision;
    }

    public String setDecision(String decision){
        this.decision = decision;
        return decision;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }
}
