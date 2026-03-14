package fashionmate_backend.models;

import jakarta.persistence.*;

@Entity
public class GlamUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trendingStyle;

    @Lob
    private String imageUrl1;
    private String altText1;

    @Lob
    private String imageUrl2;
    private String altText2;

    @Lob
    private String imageUrl3;
    private String altText3;

    //Getters and Setters

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getTrendingStyle(){
        return trendingStyle;
    }

    public void setTrendingStyle(String trendingStyle){
        this.trendingStyle = trendingStyle;
    }

    public String getImageUrl1(){
        return imageUrl1;
    }

    public void setImageUrl1(String imageUrl1){
        this.imageUrl1 = imageUrl1;
    }

    public String getAltText1(){
        return altText1;
    }

    public void setAltText1(String altText1){
        this.altText1 = altText1;
    }

    public String getImageUrl2(){
        return imageUrl2;
    }

    public void setImageUrl2(String imageUrl2){
        this.imageUrl2 = imageUrl2;
    }

    public String getAltText2(){
        return altText2;
    }

    public void setAltText2(String altText2){
        this.altText2 = altText2;
    }

    public String getImageUrl3(){
        return imageUrl3;
    }

    public void setImageUrl3(String imageUrl3){
        this.imageUrl3 = imageUrl3;
    }

    public String getAltText3(){
        return altText3;
    };

    public void setAltText3(String altText3){
        this.altText3 = altText3;
    };





}
