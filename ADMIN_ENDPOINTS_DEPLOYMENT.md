# Backend Deployment Guide - Admin Endpoints Fix

## What Was Fixed

This update adds the missing admin endpoints that were causing 500 Internal Server Error:
- `GET /api/orders/admin/stats` - Returns dashboard statistics
- `GET /api/orders/admin/all` - Returns all orders
- `PUT /api/orders/admin/{orderId}/status` - Updates order status

## Files Created/Modified

### New Files:
1. **AdminController.java** - `src/main/java/com/gaelcraves/project3/GaelCravings_Backend/Controller/AdminController.java`
   - REST controller handling admin endpoints
   - Includes error handling to return default values instead of 500 errors
   - Has logging for debugging

### Modified Files:
1. **OrderService.java** - `src/main/java/com/gaelcraves/project3/GaelCravings_Backend/Service/OrderService.java`
   - Added null safety checks in `getAdminStats()` method
   - Added try-catch to return default stats on error instead of throwing exceptions

2. **SecurityConfig.java** - `src/main/java/com/gaelcraves/project3/GaelCravings_Backend/Auth/SecurityConfig.java`
   - Added authentication requirement for `/api/orders/admin/**` endpoints
   - Ensures only authenticated users can access admin endpoints

## Deployment Instructions

### Step 1: Commit and Push Changes

```bash
cd /path/to/GaelCravings_Backend

# Check current branch (should be sergio_admin_func)
git branch

# Stage all changes
git add .

# Commit changes
git commit -m "Add admin endpoints - fix 500 error on /api/orders/admin/stats"

# Push to Heroku branch (or your deployment branch)
git push origin sergio_admin_func
```

### Step 2: Deploy to Heroku

Option A: If you have automatic deployments enabled on Heroku:
- Go to Heroku Dashboard
- Select the gaelcraves-backend app
- Navigate to Deploy tab
- Click "Deploy Branch" for sergio_admin_func

Option B: If deploying via Heroku CLI:
```bash
# Login to Heroku (if needed)
heroku login

# Deploy
git push heroku sergio_admin_func:main

# Or if main branch:
git push heroku main
```

### Step 3: Verify Build Success

```bash
# Check if deployment succeeded
heroku logs --tail --app gaelcraves-backend-256f85b120e2

# Look for these log entries:
# - "BUILD SUCCESS"
# - "Starting GaelCravingsBackendApplication"
```

### Step 4: Test the Endpoints

Test with curl commands:

```bash
# 1. Login to get JWT token (replace with real credentials)
curl -X POST https://gaelcraves-backend-256f85b120e2.herokuapp.com/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"yourpassword"}'

# Save the token from response, then test admin endpoints:

# 2. Test admin stats endpoint (replace YOUR_JWT_TOKEN)
curl -X GET https://gaelcraves-backend-256f85b120e2.herokuapp.com/api/orders/admin/stats \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Expected response:
# {"pendingOrders":0,"todayRevenue":0,"totalUsers":1,"menuItems":5}

# 3. Test all orders endpoint
curl -X GET https://gaelcraves-backend-256f85b120e2.herokuapp.com/api/orders/admin/all \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Expected response: Array of orders (might be empty: [])
```

### Step 5: Test from Frontend

After backend deployment, test from the admin panel:
1. Go to https://gaelcraves-frontend-7a6e5c03f69a.herokuapp.com
2. Login with admin credentials
3. Navigate to Admin page
4. Check if:
   - Statistics load without errors
   - Orders list displays (even if empty)
   - No 500 errors in browser console

## Troubleshooting

### Issue: Still getting 500 error

Check Heroku logs:
```bash
heroku logs --tail --app gaelcraves-backend-256f85b120e2
```

Look for:
- Database connection errors
- Null pointer exceptions
- Authentication failures

### Issue: 401 Unauthorized

- Make sure JWT token is valid and not expired
- Check that Authorization header is included: `Authorization: Bearer <token>`
- Verify SecurityConfig has admin endpoints in authenticated section

### Issue: Empty data but no errors

This is expected if:
- Database has no orders yet
- No users have been created
- Menu items haven't been added

The endpoints will return:
```json
{
  "pendingOrders": 0,
  "todayRevenue": 0,
  "totalUsers": 0,
  "menuItems": 0
}
```

### Issue: CORS errors

If you see CORS errors after deployment, verify:
1. Frontend URL is in SecurityConfig allowed origins
2. Backend is sending proper CORS headers
3. Check Heroku logs for CORS-related messages

## Database Setup (if needed)

If admin stats show all zeros but you expect data:

1. Check PostgreSQL database connection:
```bash
heroku pg:info --app gaelcraves-backend-256f85b120e2
```

2. Check if tables exist:
```bash
heroku pg:psql --app gaelcraves-backend-256f85b120e2
# Then run: \dt
# Should show: users, orders, order_items, food_items, etc.
```

3. Check if data exists:
```bash
heroku pg:psql --app gaelcraves-backend-256f85b120e2
# Then run: SELECT COUNT(*) FROM orders;
```

## Success Criteria

✅ Backend deploys without build errors
✅ GET /api/orders/admin/stats returns JSON (even if all zeros)
✅ GET /api/orders/admin/all returns JSON array (even if empty)
✅ No 500 errors in Heroku logs
✅ Frontend admin panel loads without console errors
✅ Admin stats display on frontend dashboard

## Contact

If deployment fails or you encounter issues:
1. Check Heroku logs: `heroku logs --tail --app gaelcraves-backend-256f85b120e2`
2. Send error messages from logs
3. Test endpoints with curl to isolate frontend vs backend issues
