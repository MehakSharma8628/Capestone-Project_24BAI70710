
export const GROUPS = ['O-', 'O+', 'A-', 'A+', 'B-', 'B+', 'AB-', 'AB+'];

export const CITIES = [
  'Gurugram',
  'Delhi',
  'Noida',
  'Faridabad',
  'Ghaziabad',
  'Chandigarh'
];

// Live backend URL is configured through VITE_API_URL.
// If empty, local Vite proxy will be used.
const API_URL = (import.meta.env.VITE_API_URL || '').replace(/\/$/, '');

/**
 * API wrapper:
 * Adds JWT authentication, sends requests,
 * parses JSON responses and handles errors.
 */
export async function api(path, { method = 'GET', body } = {}) {
  const headers = {
    'Content-Type': 'application/json'
  };

  const token = localStorage.getItem('token');

  if (token) {
    headers.Authorization = 'Bearer ' + token;
  }

  const res = await fetch(API_URL + '/api' + path, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined
  });

  if (!res.ok) {
    let msg = 'Request failed (' + res.status + ')';

    try {
      const data = await res.json();
      msg = data.message || data.error || msg;
    } catch {
      // Response does not contain JSON.
    }

    if (res.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }

    throw new Error(msg);
  }

  const responseText = await res.text();

  return responseText ? JSON.parse(responseText) : null;
}
