import axios from 'axios';

const api = axios.create({
  baseURL: 'https://journeymate-backend-ifbynfjw3a-ew.a.run.app/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;