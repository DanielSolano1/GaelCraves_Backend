#!/bin/bash

# GaelCraves Backend - Heroku Deployment Script
# This script sets up your Heroku environment with all necessary configurations

set -e  # Exit on error

echo "🚀 GaelCraves Backend - Heroku Setup"
echo "===================================="
echo ""

# Check if Heroku CLI is installed
if ! command -v heroku &> /dev/null; then
    echo "❌ Heroku CLI is not installed"
    echo "Install it from: https://devcenter.heroku.com/articles/heroku-cli"
    exit 1
fi

echo "✅ Heroku CLI found"
echo ""

# Get app name from user or use default
read -p "Enter your Heroku app name (default: gaelcraves-backend): " APP_NAME
APP_NAME=${APP_NAME:-gaelcraves-backend}

echo "📦 Setting up Heroku app: $APP_NAME"
echo ""

# Check if app exists, create if not
if heroku apps:info -a $APP_NAME &> /dev/null; then
    echo "✅ App '$APP_NAME' already exists"
else
    echo "Creating new Heroku app: $APP_NAME"
    heroku create $APP_NAME
fi

echo ""
echo "🗄️  Setting up PostgreSQL database..."

# Add PostgreSQL if not already added
if heroku addons:info heroku-postgresql -a $APP_NAME &> /dev/null; then
    echo "✅ PostgreSQL already configured"
else
    echo "Adding PostgreSQL addon..."
    heroku addons:create heroku-postgresql:essential-0 -a $APP_NAME
fi

echo ""
echo "🔧 Configuring environment variables..."
echo ""

# Set JWT Secret
echo "Setting JWT secret..."
JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
heroku config:set APP_JWT_SECRET="$JWT_SECRET" -a $APP_NAME

# Set JWT Expiration (24 hours)
heroku config:set JWT_EXPIRATION="86400000" -a $APP_NAME

# Set JPA DDL Auto
heroku config:set JPA_DDL_AUTO="update" -a $APP_NAME

# Set Database Pool Settings
heroku config:set DB_POOL_SIZE="5" -a $APP_NAME
heroku config:set DB_MIN_IDLE="2" -a $APP_NAME

# Set Google OAuth placeholders (user needs to fill these in)
heroku config:set GOOGLE_CLIENT_ID="NOT_SET" -a $APP_NAME
heroku config:set GOOGLE_CLIENT_SECRET="NOT_SET" -a $APP_NAME

echo ""
echo "⚠️  IMPORTANT: Set your frontend URL for CORS"
read -p "Enter your frontend URL (e.g., https://yourapp.herokuapp.com): " FRONTEND_URL

if [ -n "$FRONTEND_URL" ]; then
    heroku config:set FRONTEND_ORIGIN="$FRONTEND_URL,http://localhost:8081" -a $APP_NAME
    echo "✅ CORS configured for: $FRONTEND_URL"
else
    echo "⚠️  Skipping frontend URL - you'll need to set this later with:"
    echo "   heroku config:set FRONTEND_ORIGIN='https://your-frontend.herokuapp.com' -a $APP_NAME"
fi

echo ""
echo "📋 Current configuration:"
heroku config -a $APP_NAME

echo ""
echo "🔨 Building application..."
./gradlew clean build -x test

echo ""
echo "📤 Deploying to Heroku..."
git push heroku main

echo ""
echo "✅ Deployment complete!"
echo ""
echo "🌐 Your backend is available at:"
heroku info -a $APP_NAME | grep "Web URL"

echo ""
echo "📝 Next steps:"
echo "1. Set your Google OAuth credentials (if using Google login):"
echo "   heroku config:set GOOGLE_CLIENT_ID='your-client-id' -a $APP_NAME"
echo "   heroku config:set GOOGLE_CLIENT_SECRET='your-client-secret' -a $APP_NAME"
echo ""
echo "2. Update your frontend to use this backend URL"
echo ""
echo "3. Test your deployment:"
echo "   curl https://$APP_NAME.herokuapp.com/api/menus"
echo ""
echo "4. View logs:"
echo "   heroku logs --tail -a $APP_NAME"
echo ""
echo "🎉 Done!"
