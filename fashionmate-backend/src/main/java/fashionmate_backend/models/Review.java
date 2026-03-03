package fashionmate_backend.models;

import jakarta.persistence.*;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String comment;
    private int rating;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne
    @JoinColumn(name = "style_lens_id", nullable = false)
    private StyleLens styleLens;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment(){
        return comment;
    }

    public void setComment(String comment){
        this.comment = comment;

    }

    public int getRating(){
        return rating;
    }

    public void setRating(int rating){
        this.rating = rating;
    }

    public StyleLens getStyleLens(){
        return styleLens;
    }


    public void setStyleLens(StyleLens styleLens){
        this.styleLens = styleLens;
    }

    public Review(){

    }


}
