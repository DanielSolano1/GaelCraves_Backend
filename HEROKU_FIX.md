# 🚨 Quick Fix for Heroku 503 Error

Your backend is failing to start on Heroku due to missing Google OAuth credentials.

## Immediate Fix (Choose ONE):

### Option 1: Deploy Without Google OAuth (Fastest)
The code is already fixed - just push and configure:

```bash
cd GaelCravings_Backend

# Push the fixed code
git push heroku main

# Set minimal required config (if not already set)
heroku config:set APP_JWT_SECRET="$(openssl rand -base64 64)" -a gaelcraves-backend-256f85b120e2
heroku config:set FRONTEND_ORIGIN="https://your-frontend-url.herokuapp.com,http://localhost:8081" -a gaelcraves-backend-256f85b120e2
heroku config:set GOOGLE_CLIENT_ID="NOT_SET" -a gaelcraves-backend-256f85b120e2
heroku config:set GOOGLE_CLIENT_SECRET="NOT_SET" -a gaelcraves-backend-256f85b120e2

# Restart the app
heroku restart -a gaelcraves-backend-256f85b120e2

# Check logs
heroku logs --tail -a gaelcraves-backend-256f85b120e2
```

### Option 2: Enable Google OAuth
If you want Google login to work:

1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Create OAuth 2.0 credentials
3. Set authorized redirect URIs
4. Get your Client ID and Secret
5. Configure Heroku:

```bash
heroku config:set GOOGLE_CLIENT_ID="your-actual-client-id" -a gaelcraves-backend-256f85b120e2
heroku config:set GOOGLE_CLIENT_SECRET="your-actual-secret" -a gaelcraves-backend-256f85b120e2
heroku restart -a gaelcraves-backend-256f85b120e2
```

## What Was Fixed

1. ✅ Made Google OAuth optional - app starts without it
2. ✅ Added default values to prevent startup failures
3. ✅ GoogleAuthController only loads if credentials are provided
4. ✅ Proper error messages when OAuth is not configured

## Verify It Works

```bash
# Check if backend is running
curl https://gaelcraves-backend-256f85b120e2.herokuapp.com/api/menus

# Should return: [] or your menu data (not 503)
```

## Current Status

- ✅ Code fixed and committed
- ⏳ Waiting for Heroku deployment
- ⏳ Waiting for config vars to be set

## Need Help?

Run the automated setup script:
```bash
cd GaelCravings_Backend
./deploy-heroku.sh
```

Or check logs for specific errors:
```bash
heroku logs --tail -a gaelcraves-backend-256f85b120e2
```
