# Railway Deployment - Complete Step-by-Step Guide

## ✅ Prerequisites Check

- [x] Railway account created
- [x] GitHub repository (SmartAttendance)
- [x] Dockerfile in repository
- [x] Code pushed to GitHub

Everything is ready! Follow these exact steps:

---

## 🎯 Deploy on Railway (15 minutes)

### STEP 1: Go to Railway Dashboard
1. Open https://railway.app
2. Login with your GitHub account
3. You'll see your dashboard

### STEP 2: Create New Project
1. Click **"New Project"** button (top right)
2. Select **"Deploy from GitHub repo"**
3. Search for **"SmartAttendance"** repository
4. Click to connect it
   - Railway will ask to authorize GitHub (click "Authorize")

### STEP 3: Create MySQL Database Service
1. In the project, click **"Add Service"** (or **"+"**)
2. Select **"Add from template"**
3. Search for **"MySQL"** (or **PostgreSQL** if MySQL unavailable)
4. Click **"MySQL"** → **"Deploy"**
5. Wait ~30 seconds for database to initialize

**Railway creates it automatically with credentials - we'll get them next**

### STEP 4: Get MySQL Connection Details
1. Click on the **MySQL** service card
2. Go to **"Variables"** tab (top)
3. You should see auto-generated variables:
   - `MYSQLDATABASE`
   - `MYSQLHOST`
   - `MYSQLPASSWORD`
   - `MYSQLPORT`
   - `MYSQLUSER`

**Copy these values! You'll need them next.**

### STEP 5: Configure Web Service Environment Variables

1. Click on your **SmartAttendance** service (the Docker one)
2. Go to **"Variables"** tab → **"Add Variable"**
3. **Copy-paste each line below** into Railway (or manually add):

```
SPRING_DATASOURCE_URL=jdbc:mysql://${{ MYSQLHOST }}:${{ MYSQLPORT }}/${{ MYSQLDATABASE }}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=${{ MYSQLUSER }}
SPRING_DATASOURCE_PASSWORD=${{ MYSQLPASSWORD }}
MAIL_USERNAME=your-gmail@gmail.com
MAIL_PASSWORD=your-gmail-app-password
PORT=8080
```

**Important:** Railway automatically replaces `${{ MYSQLHOST }}` etc. with actual values from MySQL service!

### STEP 6: Get Gmail App Password (If you haven't already)

You need an **app password**, not your regular Gmail password:

1. Go to https://myaccount.google.com/security
2. Search for **"App passwords"** (left sidebar)
3. Select **"Mail"** and **"Windows Computer"** (or other OS)
4. Google generates a **16-character password**
5. Copy it → Paste as `MAIL_PASSWORD` in Railway

### STEP 7: Deploy!

1. Railway auto-detects your Dockerfile
2. Click **"Deploy"** (if not auto-deploying)
3. Watch the logs (click **"View logs"**)
4. Wait for message: **"Deployment successful"**

This takes 3-5 minutes. You'll see:
```
Started SmartAttendanceApplication in X seconds
```

### STEP 8: Get Your Live URL

1. Once deployed, go to your **SmartAttendance** service
2. Click **"Settings"** tab
3. Scroll to **"Domains"**
4. You'll see your auto-generated URL:
   ```
   https://smartattendance-production.up.railway.app
   ```

**This is your live website! 🎉**

### STEP 9: Test Your Application

Open these in your browser:

1. **Homepage:** `https://smartattendance-production.up.railway.app/`
2. **Role Selection:** `https://smartattendance-production.up.railway.app/`
3. **Teacher Page:** `https://smartattendance-production.up.railway.app/teacher`
4. **Student Page:** `https://smartattendance-production.up.railway.app/student`
5. **Register:** `https://smartattendance-production.up.railway.app/register`

---

## 🔧 Troubleshooting

### Issue: "Application failed to start" or "Connection refused"

**Solution:**
1. Check **MySQL service is running** (green status)
2. Verify **environment variables match exactly**
3. View logs: Click service → **"View logs"**
4. Check for error: `Cannot connect to database`

### Issue: "502 Bad Gateway"

**Solution:**
1. Check if application is still starting (give it 2-3 minutes)
2. Click **"Redeploy"** button
3. Check logs for errors

### Issue: "Email not sending"

**Solution:**
1. Verify app password (not regular Gmail password)
2. Check MAIL_USERNAME and MAIL_PASSWORD are correct
3. Open browser console (F12) → Network tab to see errors

### Issue: "Deployment hangs"

**Solution:**
1. Maven might be downloading dependencies (normal, can take 2-3 minutes)
2. Stop and click **"Redeploy"**
3. Check logs to see build progress

---

## 📋 Quick Checklist

- [ ] Logged into Railway account
- [ ] Connected GitHub repo to Railway
- [ ] Created MySQL service
- [ ] Set environment variables (all 6)
- [ ] Got Gmail app password
- [ ] Clicked Deploy
- [ ] Waited for "Deployment successful"
- [ ] Tested homepage loads
- [ ] Tested teacher login page
- [ ] Tested student page

---

## 🚀 What Happens After Deployment

✓ Your app runs 24/7 on Railway's servers  
✓ Database stores all data persistently  
✓ Emails send automatically via Gmail  
✓ Auto-restarts if app crashes  
✓ Free plan gives you $5/month usage  

---

## 💰 Cost

**Free tier:** $5 credit every month (usually covers small apps)  
**What you pay for:**
- Web service uptime (not much)
- MySQL database (included in free tier)
- Bandwidth

Most hobby projects stay within free tier!

---

## ❓ Still Stuck?

Post the exact error from logs, and I'll help you fix it!

---

## Next Steps After Go-Live

1. **Share your URL** with teachers and students
2. **Monitor logs** regularly: Click service → "View logs"
3. **Check database** occasionally: MySQL service stats
4. **Update code?** - Just push to GitHub, Railway auto-redeploys!

---

Good luck! 🎉 Your Smart Attendance app will be live soon!
