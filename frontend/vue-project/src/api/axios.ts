import axios from '../api/axios'


export default axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json"
  } // tu backend
});
