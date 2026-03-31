# 🔧 Stock Issue - Troubleshooting & Fix

## Issue
Stock not showing in Production module even when stock exists in database.

## Root Causes Identified

### 1. **Location Mismatch**
- LOI location and Stock location must match **exactly** (case-insensitive now)
- Example: If LOI has location "Jabalpur" but stock has "jabalpur", it won't match

### 2. **No Stock in Database**
- Stock might not be added to the database yet
- Check if Inventory & Stock module has entries

### 3. **Zero Quantity Stock**
- Stock exists but `totalQuantity` is 0 or null

---

## Fixes Applied

### Backend Changes (RollingPlanController.java)

#### 1. **Improved Eligible Stock Endpoint**
```java
@GetMapping("/eligible-stock/{loiItemId}")
```
**Changes:**
- Now fetches ALL stock first, then filters by location
- Uses case-insensitive location matching (`equalsIgnoreCase`)
- Better console logging to debug issues
- Shows total stock count, location-filtered count, and eligible count

#### 2. **Added Debug Endpoint**
```java
@GetMapping("/debug/all-stock")
```
**Purpose:**
- View all stock in database
- Check locations, materials, sections, grades
- Verify quantities

---

## How to Debug

### Step 1: Check All Stock
Open browser and go to:
```
http://localhost:8080/api/rolling-plan/debug/all-stock
```

This will show:
- All stock items in database
- Their locations, materials, sections, grades, quantities
- Console will print detailed information

### Step 2: Check LOI Location
1. Go to LOI module
2. Check the location field in your LOI
3. Note the exact spelling and case

### Step 3: Verify Stock Location Matches
1. Go to Inventory & Stock module
2. Check if stock exists with the SAME location as LOI
3. Verify quantity > 0

### Step 4: Check Console Logs
When you click "Select Stock" in Production:
1. Open browser console (F12)
2. Check backend console (where Spring Boot is running)
3. Look for debug messages:
   ```
   🔍 Checking eligible stock for LOI Item ID: X
   📍 Location: Jabalpur
   🟦 Total stock items in database: Y
   📍 Stock items in location 'Jabalpur': Z
   ✅ Eligible Stock Count: N
   ```

---

## Common Issues & Solutions

### Issue 1: "No eligible stock found"
**Cause:** Location mismatch or no stock in that location
**Solution:**
1. Check LOI location
2. Add stock with matching location in Inventory module
3. Ensure quantity > 0

### Issue 2: Stock shows but can't select
**Cause:** Frontend issue or stock already allocated
**Solution:**
1. Refresh page
2. Check if stock is already used in another plan
3. Clear browser cache

### Issue 3: Location case sensitivity
**Cause:** "Jabalpur" vs "jabalpur"
**Solution:** 
- Fixed! Now uses case-insensitive matching
- Both will work

---

## Testing Steps

### 1. Add Test Stock
Go to Inventory & Stock module and add:
```
Location: Jabalpur
Material: Angel
Grade: MS
Section: 100X100X8
Length: 8000
Quantity: 10
```

### 2. Create LOI
Go to LOI module and create:
```
Location: Jabalpur
Items:
  - Material: Angel
    Grade: MS
    Section: 100X100X8
    Length: 8000
    Quantity: 5
```

### 3. Create Production Plan
1. Go to Production module
2. Select the LOI
3. Click "Select Stock" for the item
4. Stock should now appear!

---

## API Endpoints for Testing

### Get All Stock
```
GET http://localhost:8080/api/stock
```

### Get Stock by Location
```
GET http://localhost:8080/api/stock/location/Jabalpur
```

### Get Eligible Stock for LOI Item
```
GET http://localhost:8080/api/rolling-plan/eligible-stock/{loiItemId}
```

### Debug All Stock (NEW)
```
GET http://localhost:8080/api/rolling-plan/debug/all-stock
```

---

## Quick Fix Checklist

- [ ] Backend server running
- [ ] Database has stock entries
- [ ] Stock location matches LOI location (case doesn't matter now)
- [ ] Stock quantity > 0
- [ ] LOI status is "Pending" or "Planned"
- [ ] Browser cache cleared
- [ ] Check backend console logs

---

## If Still Not Working

1. **Check Database Directly:**
   ```sql
   SELECT * FROM rm_stock WHERE location = 'Jabalpur';
   SELECT * FROM loi WHERE location = 'Jabalpur';
   ```

2. **Restart Backend Server:**
   - Stop Spring Boot
   - Clean build: `mvn clean install`
   - Restart

3. **Clear Frontend Cache:**
   - Hard refresh: Ctrl + Shift + R
   - Clear browser cache
   - Restart Angular: `ng serve`

---

## Contact

If issue persists after following all steps:
1. Share backend console logs
2. Share browser console logs
3. Share screenshot of stock and LOI data

**Last Updated:** 2025-11-05
**Status:** ✅ Fixed - Case-insensitive location matching added
