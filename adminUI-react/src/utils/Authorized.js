import RenderAuthorized from '@/components/Authorized';
import { getAuthority } from './authority';

let Authorized = RenderAuthorized(getAuthority()); // eslint-disable-line

// Reload the rights component
const reloadAuthorized = () => {
  Authorized = RenderAuthorized(getAuthority());
};
const isAdminUser = () => {
  return getAuthority() == "role_super";
};
const isLogined = () => {
  console.log("getAuthority:"+getAuthority());
  return getAuthority() != "guest";
};
export { reloadAuthorized };
export { isAdminUser };
export { isLogined };
export default Authorized;
