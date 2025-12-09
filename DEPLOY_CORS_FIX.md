# Deploy CORS Fix to Heroku

## What was fixed?
Updated `SecurityConfig.java` to allow requests from the frontend at `https://gaelcraves-frontend-7a6e5c03f69a.herokuapp.com`

## Steps to deploy:

### 1. Pull the latest changes
```bash
cd GaelCravings_Backend
git pull origin sergio_admin_func
# Or merge the sergio_admin_func branch into main
```

### 2. Set the environment variable on Heroku (IMPORTANT!)
```bash
heroku config:set FRONTEND_ORIGIN=https://gaelcraves-frontend-7a6e5c03f69a.herokuapp.com -a gaelcraves-backend-256f85b120e2
```

### 3. Deploy to Heroku
```bash
git push heroku main
# Or if you're on a different branch:
git push heroku sergio_admin_func:main
```

### 4. Verify the deployment
Check that the backend is running:
```bash
heroku logs --tail -a gaelcraves-backend-256f85b120e2
```

### 5. Test CORS
Run this command to verify CORS is working:
```bash
curl -H "Origin: https://gaelcraves-frontend-7a6e5c03f69a.herokuapp.com" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS \
     https://gaelcraves-backend-256f85b120e2.herokuapp.com/api/users/login \
     -v
```

You should see `Access-Control-Allow-Origin: https://gaelcraves-frontend-7a6e5c03f69a.herokuapp.com` in the response headers.

## Changes Made:
- Updated `SecurityConfig.java` line 63 to include the frontend Heroku URL
- The backend now accepts requests from:
  - Local development servers (localhost:3000, 8081, 19006)
  - Frontend production: `https://gaelcraves-frontend-7a6e5c03f69a.herokuapp.com`
  - Any URL set in the `FRONTEND_ORIGIN` environment variable

## Note:
The environment variable `FRONTEND_ORIGIN` must be set on Heroku for the fix to work properly!
