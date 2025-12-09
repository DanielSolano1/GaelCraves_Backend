#!/bin/bash

# ============================================
# Script to Update Heroku Database with Nutritional Columns and Complete Menu
# ============================================

echo "🔧 Updating GaelCraves Database on Heroku..."
echo "=============================================="
echo ""

# Check if psql is available
if ! command -v psql &> /dev/null; then
    echo "❌ PostgreSQL client (psql) is not installed"
    echo "📦 Installing postgresql via Homebrew..."
    brew install postgresql
fi

# Database connection details from Heroku config
DB_URL="postgresql://postgres.cfmztcfqrvinpwxqqwxa:chabyss*@aws-1-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require"

echo "Step 1: Adding nutritional columns to food_item table..."
echo "---------------------------------------------------"
PGPASSWORD="chabyss*" psql "$DB_URL" -f scripts/add_nutritional_columns.sql

if [ $? -eq 0 ]; then
    echo "✅ Nutritional columns added successfully!"
else
    echo "❌ Failed to add nutritional columns"
    exit 1
fi

echo ""
echo "Step 2: Populating complete menu with nutritional data..."
echo "---------------------------------------------------"
PGPASSWORD="chabyss*" psql "$DB_URL" -f scripts/populate_complete_menu.sql

if [ $? -eq 0 ]; then
    echo "✅ Complete menu populated successfully!"
else
    echo "❌ Failed to populate menu"
    exit 1
fi

echo ""
echo "=========================================="
echo "✅ Database update complete!"
echo "=========================================="
echo ""
echo "📊 Summary:"
echo "  - Added columns: protein, carbohydrates, fat, description, image_url, category, is_available"
echo "  - Added 15 food items across 4 categories"
echo "  - Categories: Burgers & Sandwiches, Wings, Sides, Beverages"
echo ""
echo "🚀 Next steps:"
echo "  1. Build and deploy backend: cd GaelCravings_Backend && ./gradlew build && git push heroku main"
echo "  2. Test API: curl https://gaelcraves-backend-256f85b120e2.herokuapp.com/api/food-items"
