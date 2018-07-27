package asim.tgs_member_app.models;


import asim.tgs_member_app.restclient.BaseModel;

public class MOUser extends BaseModel {

   private int itemNumber;
   private int userId;
   private String accessToken;
   private String email;
   private String fullName;
   private String password;
   private boolean verified;
   private boolean passwordTemporary;

   public int getItemNumber() {
      return itemNumber;
   }

   public void setItemNumber(int itemNumber) {
      this.itemNumber = itemNumber;
   }

   public int getUserId() {
      return userId;
   }

   public void setUserId(int userId) {
      this.userId = userId;
   }

   public String getAccessToken() {
      return accessToken;
   }

   public void setAccessToken(String accessToken) {
      this.accessToken = accessToken;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getFullName() {
      return fullName;
   }

   public void setFullName(String fullName) {
      this.fullName = fullName;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public boolean isVerified() {
      return verified;
   }

   public void setVerified(boolean verified) {
      this.verified = verified;
   }

   public boolean isPasswordTemporary() {
      return passwordTemporary;
   }

   public void setPasswordTemporary(boolean passwordTemporary) {
      this.passwordTemporary = passwordTemporary;
   }
}
