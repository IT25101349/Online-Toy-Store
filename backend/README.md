# Online Toy Shop Backend

This backend implements the Toy Management module using Java, Spring Boot, REST APIs, text-file storage, and multipart image upload.

## Folder Structure

```text
Abi_Toy_Management/
|-- frontend/
|-- backend/
|   |-- uploads/
|   |   `-- toys/
|   |-- src/
|   |   `-- main/
|   |       |-- java/com/abitoymanagement/backend/
|   |       |   |-- config/
|   |       |   |-- controller/
|   |       |   |-- dto/
|   |       |   |-- exception/
|   |       |   |-- model/
|   |       |   |-- repository/
|   |       |   |-- service/
|   |       |   `-- util/
|   |       `-- resources/
|   |           `-- application.properties
|   |-- pom.xml
|   `-- README.md
`-- database/
    |-- toys.txt
    |-- users.txt
    |-- orders.txt
    `-- reviews.txt
```

## Main Features

- Add a toy with `multipart/form-data`
- View all toys
- View one toy by ID
- Update toy details with optional image replacement
- Delete a toy and its uploaded image
- Search by name, category, or brand
- Store toy data in `../database/toys.txt`
- Store images in `uploads/toys/`
- Serve uploaded images through an API endpoint

## API Endpoints

- `POST /api/toys`
- `GET /api/toys`
- `GET /api/toys/{id}`
- `PUT /api/toys/{id}`
- `DELETE /api/toys/{id}`
- `GET /api/toys/search?keyword=car`
- `GET /api/toys/image/{filename}`

## Example Request Formats

### 1. Add Toy

```bash
curl -X POST "http://localhost:8081/api/toys" \
  -H "Content-Type: multipart/form-data" \
  -F "toyId=T004" \
  -F "name=Galaxy Robot" \
  -F "category=Electronic" \
  -F "brand=FunTech" \
  -F "price=49.99" \
  -F "quantity=10" \
  -F "ageGroup=8+" \
  -F "description=Interactive dancing robot with lights." \
  -F "image=@C:/images/robot.png"
```

### 2. Update Toy

```bash
curl -X PUT "http://localhost:8081/api/toys/T004" \
  -H "Content-Type: multipart/form-data" \
  -F "toyId=T004" \
  -F "name=Galaxy Robot Pro" \
  -F "category=Electronic" \
  -F "brand=FunTech" \
  -F "price=59.99" \
  -F "quantity=7" \
  -F "ageGroup=8+" \
  -F "description=Updated robot with music and voice control." \
  -F "image=@C:/images/robot-new.jpg"
```

### 3. Search Toys

```bash
curl "http://localhost:8081/api/toys/search?keyword=lego"
```

### 4. JavaScript Frontend Example

```javascript
const formData = new FormData();
formData.append("toyId", "T005");
formData.append("name", "Mini Blocks Set");
formData.append("category", "Construction");
formData.append("brand", "BlockTown");
formData.append("price", "24.50");
formData.append("quantity", "18");
formData.append("ageGroup", "6+");
formData.append("description", "Creative block set for beginners.");
formData.append("image", imageInput.files[0]);

fetch("http://localhost:8081/api/toys", {
  method: "POST",
  body: formData
})
  .then((response) => response.json())
  .then((data) => console.log(data));
```

## How To Run Locally

1. Open a terminal in the `backend` folder.
2. Run `mvn clean spring-boot:run`
3. The app and API start at `http://localhost:8081`
4. Uploaded images will be saved to `backend/uploads/toys/`
5. Toy records will be stored in `database/toys.txt`

## Notes

- No database is used.
- The backend is ready to connect to an HTML, CSS, and JavaScript frontend.
- CORS is enabled for common local frontend ports and can be changed in `application.properties`.
