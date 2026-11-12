
export const GROUPS = [
  'O-', 'O+', 'A-', 'A+', 'B-', 'B+', 'AB-', 'AB+'
];

export const CITIES = [
  'Gurugram',
  'Delhi',
  'Noida',
  'Faridabad',
  'Ghaziabad',
  'Chandigarh'
];

const API_URL = (
  import.meta.env.VITE_API_URL ||
  'http://localhost:8080'
).replace(/\/$/, '');

export async function api(path, { method = 'GET', body } = {}) {
  const headers = {};

  const publicRequest =
    path.startsWith('/donors/search') ||
    path.startsWith('/banks');

  // Send token only for protected requests
  const token = localStorage.getItem('token');

  if (token && !publicRequest) {
    headers.Authorization = `Bearer ${token}`;
  }

  if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }

  let res;

  try {
    res = await fetch(`${API_URL}/api${path}`, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined
    });
  } catch {
    throw new Error('Cannot connect to backend server.');
  }

  if (!res.ok) {
    let msg = `Request failed (${res.status})`;

    try {
      const data = await res.json();
      msg = data.message || data.error || msg;
    } catch {}

    throw new Error(msg);
  }

  const responseText = await res.text();

  if (!responseText) return null;

  try {
    return JSON.parse(responseText);
  } catch {
    throw new Error('Invalid response received from server.');
  }
}
