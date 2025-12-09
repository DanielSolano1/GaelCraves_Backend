#!/bin/bash

# Populate GaelCraves Menu Items
# This script adds all menu items via the backend API

API_BASE="https://gaelcraves-backend-256f85b120e2.herokuapp.com/api"

echo "🍔 Populating GaelCraves Menu..."
echo "================================"
echo ""

# Get menu ID
echo "📋 Fetching menu ID..."
MENU_ID=$(curl -s "${API_BASE}/menus" | grep -o '"menuId":[0-9]*' | head -1 | cut -d: -f2)
echo "Menu ID: $MENU_ID"
echo ""

if [ -z "$MENU_ID" ]; then
    echo "❌ Could not find menu ID"
    exit 1
fi

# Function to add food item
add_item() {
    local name="$1"
    local price="$2"
    local calories="$3"
    local protein="$4"
    local carbs="$5"
    local fat="$6"
    local category="$7"
    local description="$8"
    
    echo "Adding: $name..."
    
    curl -s -X POST "${API_BASE}/food-items" \
        -H "Content-Type: application/json" \
        -d "{
            \"name\": \"$name\",
            \"price\": $price,
            \"calories\": $calories,
            \"protein\": $protein,
            \"carbohydrates\": $carbs,
            \"fat\": $fat,
            \"category\": \"$category\",
            \"description\": \"$description\",
            \"isAvailable\": true,
            \"menu\": {\"menuId\": $MENU_ID}
        }" > /dev/null
    
    if [ $? -eq 0 ]; then
        echo "✅ Added: $name"
    else
        echo "❌ Failed: $name"
    fi
}

echo "🍗 Adding CHICKEN SANDWICH COMBOS from GaelCraves Menu..."
echo "----------------------------------------------------------"

# Combo #1 - Chicken Sandwich + Fries
add_item "#1 Chicken Sandwich + Fries" 12.00 800 60 65 35 "COMBO" "Chicken sandwich with fries - Choose Spicy or Original"

# Combo #2 - Two Chicken Sandwiches
add_item "#2 Two Chicken Sandwiches" 15.00 1100 113 78 50 "COMBO" "Two chicken sandwiches - Choose Spicy or Original"

# Combo #3 - Two Chicken Sandwiches + Large Fries
add_item "#3 Two Chicken Sandwiches + Large Fries" 20.00 1400 118 95 60 "COMBO" "Two chicken sandwiches with large fries - Choose Spicy or Original"

echo ""
echo "🔥 Adding PROTEIN MAC BOWL..."
echo "-----------------------------"

# Protein Mac Bowl (TOP SELLER)
add_item "Protein Mac Bowl" 15.00 850 65 78 35 "ENTREE" "Fries, Protein Mac & Cheese, Crispy Chicken Breast, Low-Cal Sauce - Spicy option available"

echo ""
echo "➕ Adding ADD-ONS..."
echo "--------------------"

# Add-ons
add_item "Signature Sauce Dip" 1.00 50 0 2 5 "ADDON" "Signature sauce dip"
add_item "Double Fries" 3.00 365 5 48 17 "ADDON" "Extra portion of fries"

echo ""
echo "✅ Menu population complete!"
echo ""
echo "🔍 Checking total items..."
TOTAL=$(curl -s "${API_BASE}/food-items" | grep -o '"foodItemId"' | wc -l | tr -d ' ')
echo "Total food items in database: $TOTAL"
echo ""
echo "🌐 View menu at: https://gaelcraves-frontend-7a6e5c03f69a.herokuapp.com/order"
