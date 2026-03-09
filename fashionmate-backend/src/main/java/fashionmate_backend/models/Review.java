package fashionmate_backend.models;

import jakarta.persistence.*;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String comment;
    private int rating;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {

        return user;
    }

    public void setUser(User user) {

        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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



    public Review(){

    }


}
