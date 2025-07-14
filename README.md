# Registration API

> This project (originally named **Cadastro API**) is a RESTful API designed to manage user registration, authentication, password updates, email validations and administrative operations.

#### Technologies ####

+ Java 8
+ WildFly 20.0.1.final
+ JAX-RS  (RESTeasy, WildFly version)
+ CDI (Context & Dependency Injection, WildFly Version)
+ JavaMail (email confirmation)
+ JWT 0.11.5 (JJWT)
+ ARGON2  2.7 (password Hashing)
+ OpenAPI / Swagger 3
+ SLF4J 1.7.36
+ LOGBACK 1.2.13
  
#### DataBase connection ####
+ MYSQL 8.0.39
+ MYSQL Connector/J 8.0.33
+ JPA/HIBERNATE (dataBase persistence)

#### TEST ####
+ JUnit 5  5.10.0 
+ Mockito-Junit-Jupiter 4.11.0
+ Mockito Core 4.11.0

####  Demo: API in Action

If you prefer not to configure the project locally, here’s a short demo playlist showing the main endpoints working in real time:

 [Watch the API Demo on YouTube](https://www.youtube.com/playlist?list=PL4KSDC4TNQZ__DFzvUDtSAJ9XF7mXKyeS)

Each video is 10 seconds to 1 minute long and demonstrates one or more related endpoints:
1. **Register**  
   Endpoints for user registration and registration code validation.

2. **Login**  
   Endpoints for user login and logout.

3. **Update Username**  
   Intuitive endpoint to change the user's username.

4. **Delete User**  
   Simple and direct endpoint to delete the user account.

5. **Update Password**  
   Endpoints for password recovery: start, validate code, and update password.

6. **Update Email**  
   Endpoints for email change and code validation.

7. **Admin**  
   Endpoints for searching users and promoting a user to admin.

####  Environment Configuration

Before running the application, you must configure the following settings:

##### 1. Database Configuration (`persistence.xml`)

Edit the `persistence.xml` file and set your MySQL credentials:

```xml
 <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/YOUR_DB_NAME?useSSL=false&amp;allowPublicKeyRetrieval=true&amp;serverTimezone=UTC"/>
<property name="javax.persistence.jdbc.user" value="YOUR_DB_USER" />
<property name="javax.persistence.jdbc.password" value="YOUR_DB_PASSWORD" />
```  
#### 2. SMPT configuration(web.xml)

Add your SMTP settings to the web.xml file (used for sending confirmation and recovery emails):

```xml
 <context-param>
        <param-name>email.smtp.user</param-name>
        <param-value>your_email@gmail.com</param-value>
    </context-param>

    <context-param>
        <param-name>email.smtp.password</param-name>
        <param-value>your_password</param-value>
    </context-param>
```
### How to Run the Application

Follow these steps to run the project locally:

##### 1. Requirements

Make sure you have the following installed:

- **Java 8**
- **WildFly 20.0.1.Final**
- **MySQL 8+**
- (Optional) A tool like **Hoppscotch** or **Postman** to test the endpoints.
  If you are using Intellij idea this project contains the .http files for endpoint testing.

##### 2. Clone the repository

```bash
git clone https://github.com/Maik-Furtado/registration-api.git
cd registration-api
```

#### 3.  Configure the environment

+ Edit the `persistence.xml` file with your MySQL credentials.
+ Edit the web.xml file with your SMTP configuration ([See Environment Configuration](#environment-configuration)).
+ Ensure WildFly is configured to allow HTTPS access on port 8443 in standalone.xml.

#### 4. Build and Deploy Application

+ Package the application  into a `.war` file (e.g., using your IDE or `mvn package`.
+ Copy the `.war` file to the WildFly `deployments/` folder
+ Start the WildFly.
+ Access the API.
+ Base URL: `https://localhost:8443/cadastroapi/actrl/api`.
+ Exemple endpoint `POST user/register`.

#### OpenAPI Specification

You can explore all endpoints and request/response models using the [OpenAPI file](docs/openapi.yaml).

To visualize it interactively, import the file into [Swagger Editor](https://editor.swagger.io/) or any compatible tool.

#### Sample Data for Testing

You can populate your database with sample users using the script provided:

Admin:

`UPDATE user
SET username = ' Admin',
    email = 'testemail@example.com',
    role = 'ADMIN'
WHERE id = YOUR-ID-HERE;`

Fake users for test.

`INSERT INTO user (id, username, email, passwordhash, status,role)
VALUES
  (201, 'ana silva', 'anasilva@example.com', '123456', 'VALIDATED','USER'),
  (202, 'bruno ferreira', 'brunoferreira@example.com', '123456', 'VALIDATED','USER'),
  (203, 'carla martins', 'carlamartins@example.com', '123456', 'VALIDATED','USER'),
  (204, 'danilo souza', 'danilosouza@example.com', '123456', 'VALIDATED','USER'),
  (205, 'elisa rocha', 'elisarocha@example.com', '123456', 'VALIDATED','USER'),
  (206, 'felipe almeida', 'felipealmeida@example.com', '123456', 'VALIDATED','USER'),
  (207, 'gabriela pinto', 'gabrielapinto@example.com', '123456', 'VALIDATED','USER'),
  (208, 'henrique costa', 'henriquecosta@example.com', '123456', 'VALIDATED','USER'),
  (209, 'isabela lima', 'isabelalima@example.com', '123456', 'VALIDATED','USER'),
  (210, 'joao ramos', 'joaoramos@example.com', '123456', 'VALIDATED','USER');`

 
Thank you for visiting.  
I hope this documentation was helpful to you!

— Maik Anton Furtado.


