# AndroFade Setup Guide

## Quick Start (30 seconds)

### Step 1: Build APK
```bash
Android Studio → Build → Build APK
```

### Step 2: Install on Phone 1
```
1. Transfer APK to Phone 1
2. Tap to install
3. [ALLOW ALL PERMISSIONS] ← 1 Click!
4. Open app
5. Server IP: 192.168.1.100 (or your computer IP)
6. Port: 5000
7. [START] ← 1 Click!
```

### Step 3: Run Server on Computer
```bash
cd src/AndroratServer
javac *.java
java server.Main
```

### Step 4: Control from GUI
- Select Phone 1 from list
- Send SMS, Call, Get GPS, etc.

---

## Features

✅ **SMS Control** - Send/Receive messages
✅ **Call Control** - Make calls remotely
✅ **GPS Tracking** - Real-time location
✅ **Camera** - Take photos/videos
✅ **Audio** - Record microphone
✅ **Contacts** - Read all contacts
✅ **Files** - Browse and download
✅ **Battery Info** - Monitor battery level
✅ **Network Info** - Check connection

---

## System Requirements

### Phone 1 (Client)
- Android 8.0+ (Optimized for Android 12+)
- 100MB free space
- Internet connection (WiFi or 4G)

### Phone 2 (Target)
- Any Android version
- Can be offline for some features

### Computer (Server)
- Windows/Mac/Linux
- Java 11+
- Python 3.6+ (optional for advanced setup)

---

## Network Setup

### Same WiFi Network
```
Computer: 192.168.1.100:5000
Phone 1: Connect to same WiFi
Phone 2: Any network
```

### Different Networks
```
Use Port Forwarding:
1. Open router settings
2. Forward port 5000 to computer
3. Use external IP in Phone 1
```

---

## Troubleshooting

### "Cannot connect to server"
- Check firewall settings
- Verify IP address is correct
- Ensure server is running
- Try ping: ping [computer IP]

### "Permissions denied"
- Tap [ALLOW] when prompted
- Go to Settings → Apps → AndroFade → Permissions
- Enable all permissions manually

### "GPS not working"
- Enable Location Services on Phone 1
- Use GPS provider (not Network only)
- Wait 30 seconds for first fix

### "Audio recording fails"
- Check microphone is not in use
- Grant RECORD_AUDIO permission
- Try restart the app

---

## Legal Notice

⚠️ **IMPORTANT:**

- Only use on devices you own
- Unauthorized access is ILLEGAL
- This tool is for educational purposes
- Misuse may result in criminal charges
- Always get written permission before use

---

## Support

For issues or questions:
1. Check README.md
2. Review GitHub Issues
3. Test on emulator first

---

**Version:** 1.0
**Last Updated:** 2026-05-14
**License:** Educational Use Only
