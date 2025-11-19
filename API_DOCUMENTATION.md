# Student Service API Documentation

Base URL: `/api/v1/students`

All responses follow the `ApiResponse<T>` format:
```json
{
  "success": true,
  "message": "string",
  "data": {},
  "timestamp": "2024-01-01T00:00:00"
}
```

---

## 1. Create Student
**POST** `/api/v1/students`

**Authorization:** Requires `ADMIN` role

**Headers:**
- `Authorization` (optional): Bearer token
- `X-User-Id` (optional): User ID

**Request Body:**
```json
{
  "fullName": "string (required, 3-255 chars)",
  "cpf": "string (required, 11 digits)",
  "email": "string (required, valid email)",
  "phone": "string (optional, 10-11 digits)",
  "birthDate": "YYYY-MM-DD (required, past date)",
  "registrationNumber": "string (optional, auto-generated if not provided)",
  "course": "string (required, max 255 chars)",
  "semester": "integer (required, 1-20)",
  "enrollmentDate": "YYYY-MM-DD (required)",
  "status": "ACTIVE|INACTIVE|GRADUATED|SUSPENDED|TRANSFERRED|DROPPED (optional)",
  "notes": "string (optional)"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Aluno criado com sucesso",
  "data": {
    "id": 1,
    "fullName": "string",
    "cpf": "string",
    "email": "string",
    "phone": "string",
    "birthDate": "YYYY-MM-DD",
    "age": 20,
    "registrationNumber": "string",
    "course": "string",
    "semester": 1,
    "enrollmentDate": "YYYY-MM-DD",
    "status": "ACTIVE",
    "addressStreet": "string",
    "addressNumber": "string",
    "addressComplement": "string",
    "addressNeighborhood": "string",
    "addressCity": "string",
    "addressState": "string",
    "addressZipcode": "string",
    "emergencyContactName": "string",
    "emergencyContactPhone": "string",
    "emergencyContactRelationship": "string",
    "notes": "string",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "createdBy": "string",
    "updatedBy": "string"
  }
}
```

---

## 2. Get Student by ID
**GET** `/api/v1/students/{id}`

**Path Parameters:**
- `id`: Long (numeric only)

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Operação realizada com sucesso",
  "data": {
    // StudentResponseDTO (same structure as Create response)
  }
}
```

---

## 3. Get Student by Registration Number
**GET** `/api/v1/students/registration/{registrationNumber}`

**Path Parameters:**
- `registrationNumber`: String

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    // StudentResponseDTO
  }
}
```

---

## 4. List All Students (Paginated)
**GET** `/api/v1/students`

**Query Parameters:**
- `page`: int (default: 0)
- `size`: int (default: 20)
- `sortBy`: string (default: "id")
- `direction`: "ASC" | "DESC" (default: "ASC")

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "content": [
      {
        // StudentSummaryDTO (simplified student data)
      }
    ],
    "pageable": {},
    "totalElements": 100,
    "totalPages": 5,
    "size": 20,
    "number": 0
  }
}
```

---

## 5. Search Students
**GET** `/api/v1/students/search`

**Query Parameters:**
- `name`: string (optional)
- `course`: string (optional)
- `semester`: integer (optional)
- `status`: "ACTIVE"|"INACTIVE"|"GRADUATED"|"SUSPENDED"|"TRANSFERRED"|"DROPPED" (optional)
- `page`: int (default: 0)
- `size`: int (default: 20)
- `sortBy`: string (default: "fullName")
- `direction`: "ASC" | "DESC" (default: "ASC")

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    // Page<StudentSummaryDTO>
  }
}
```

---

## 6. Get Students by Course
**GET** `/api/v1/students/course/{course}`

**Path Parameters:**
- `course`: String

**Query Parameters:**
- `page`: int (default: 0)
- `size`: int (default: 20)

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    // Page<StudentSummaryDTO>
  }
}
```

---

## 7. Get Students by Course and Semester
**GET** `/api/v1/students/course/{course}/semester/{semester}`

**Path Parameters:**
- `course`: String
- `semester`: Integer

**Query Parameters:**
- `page`: int (default: 0)
- `size`: int (default: 20)

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    // Page<StudentSummaryDTO>
  }
}
```

---

## 8. Update Student
**PUT** `/api/v1/students/{id}`

**Path Parameters:**
- `id`: Long (numeric only)

**Headers:**
- `Authorization` (optional): Bearer token
- `X-User-Id` (optional): User ID

**Request Body:**
```json
{
  // Same structure as Create Student (StudentRequestDTO)
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Aluno atualizado com sucesso",
  "data": {
    // StudentResponseDTO
  }
}
```

---

## 9. Update Student Status
**PATCH** `/api/v1/students/{id}/status`

**Path Parameters:**
- `id`: Long (numeric only)

**Query Parameters:**
- `status`: "ACTIVE"|"INACTIVE"|"GRADUATED"|"SUSPENDED"|"TRANSFERRED"|"DROPPED" (required)

**Headers:**
- `Authorization` (optional): Bearer token
- `X-User-Id` (optional): User ID

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Status do aluno atualizado com sucesso",
  "data": {
    // StudentResponseDTO
  }
}
```

---

## 10. Delete Student (Soft Delete)
**DELETE** `/api/v1/students/{id}`

**Path Parameters:**
- `id`: Long (numeric only)

**Headers:**
- `Authorization` (optional): Bearer token
- `X-User-Id` (optional): User ID

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Aluno deletado com sucesso",
  "data": null
}
```

---

## 11. Restore Student
**POST** `/api/v1/students/{id}/restore`

**Path Parameters:**
- `id`: Long (numeric only)

**Headers:**
- `Authorization` (optional): Bearer token
- `X-User-Id` (optional): User ID

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Aluno restaurado com sucesso",
  "data": {
    // StudentResponseDTO
  }
}
```

---

## 12. Get Statistics
**GET** `/api/v1/students/statistics`

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "active": 50,
    "inactive": 10,
    "graduated": 30,
    "suspended": 5,
    "total": 95
  }
}
```

---

## 13. Count Students by Course
**GET** `/api/v1/students/count/course/{course}`

**Path Parameters:**
- `course`: String

**Response:** `200 OK`
```json
{
  "success": true,
  "data": 25
}
```

---

## 14. Get Students by IDs (Batch)
**POST** `/api/v1/students/batch`

**Request Body:**
```json
[1, 2, 3, 4, 5]
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": [
    {
      // Map<String, Object> with student data
    }
  ]
}
```

**Note:** If empty array or null is sent, returns empty list with message "Nenhum ID fornecido"

---

## StudentStatus Enum Values
- `ACTIVE`
- `INACTIVE`
- `GRADUATED`
- `SUSPENDED`
- `TRANSFERRED`
- `DROPPED`

---

## Notes for Load Testing

1. **Authentication:** Most endpoints accept optional `Authorization` header and `X-User-Id` header
2. **Pagination:** List endpoints support pagination with `page`, `size`, `sortBy`, and `direction` parameters
3. **Path Validation:** ID endpoints use regex `\d+` (numeric only)
4. **Response Format:** All responses wrap data in `ApiResponse<T>` structure
5. **Status Codes:** 
   - `200 OK` for successful GET/PUT/PATCH/DELETE
   - `201 Created` for successful POST (create)
   - `400 Bad Request` for validation errors
   - `401 Unauthorized` for missing/invalid auth
   - `403 Forbidden` for insufficient permissions
   - `404 Not Found` for non-existent resources

