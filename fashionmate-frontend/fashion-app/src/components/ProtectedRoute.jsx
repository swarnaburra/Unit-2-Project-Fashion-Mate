import { Navigate } from 'react-router-dom';
import { useUser } from '../context/UserContext';

function ProtectedRoute({ element, requireAuth = true }) {
  const { userId, loading } = useUser();

  if (loading) {
    return <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>Loading...</div>;
  }

  if (requireAuth && !userId) {
    return <Navigate to="/signup" replace />;
  }

  return element;
}

export default ProtectedRoute;
