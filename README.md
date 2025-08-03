# NoteX - Modern Material 3 Note-Taking App

NoteX is a modern, offline note-taking application built with Material 3 design principles. It offers a clean, intuitive interface for creating, organizing, and managing your notes and notebooks.

## ✨ Features

### 📝 Note Management
- Create, edit, and delete notes with rich text support
- Organize notes into notebooks for better categorization
- Search functionality across all notes and notebooks
- Export individual notes or entire notebooks

### 🎨 Modern Design
- **Material 3 Design**: Fully implemented with the latest Material Design 3 components
- **Dynamic Colors**: Beautiful color system with proper contrast and accessibility
- **Smooth Animations**: Fluid transitions and interactions throughout the app
- **Nested Scrolling**: Hidden scrollbars with smooth scrolling behavior
- **Modern Typography**: Clean, readable fonts with proper hierarchy

### 📱 User Experience
- **Offline-First**: Works completely offline, no internet required
- **Fast Performance**: Optimized for smooth performance on all devices
- **Intuitive Navigation**: Easy-to-use interface with clear visual hierarchy
- **Material 3 Components**: Text fields, cards, bottom sheets, and more

### 🔧 Functionality
- **Export Features**: Export notes as text files or share them directly
- **Data Management**: Clear all data option with confirmation dialogs
- **Settings**: Modern settings screen with organized preferences
- **Snackbar Notifications**: Non-intrusive feedback using Material 3 snackbars

## 🏗️ Technical Improvements

### Architecture & Build System
- **Latest Gradle**: Updated to Gradle 8.10.2 with modern build configuration
- **Material 3**: Fully migrated from Material Components to Material 3
- **Android SDK**: Updated to target API 35 with minimum API 24
- **Signed APKs**: Configured for release builds with proper signing

### UI/UX Enhancements
- **CoordinatorLayout**: Proper layout structure with app bars and nested scrolling
- **Material Cards**: Modern card designs with proper elevation and corners
- **Text Input Layouts**: Material 3 text fields with proper styling
- **Bottom Sheets**: Redesigned with drag handles and proper Material 3 styling
- **Toolbars**: Material 3 toolbars with proper navigation and actions

### Code Quality
- **Modern Components**: Replaced legacy views with Material 3 equivalents
- **Proper Error Handling**: Added try-catch blocks for file operations
- **Clean Code**: Improved code organization and readability
- **Material Theming**: Comprehensive theme system with proper color tokens

## 🚀 Build & Development

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17
- Android SDK API 35

### Building the App
```bash
# Clone the repository
git clone <repository-url>
cd NoteX

# Build debug APK
./gradlew assembleDebug

# Build release APK (signed)
./gradlew assembleRelease
```

### GitHub Actions CI/CD
The project includes automated CI/CD pipeline that:
- Builds APKs automatically on push
- Signs release APKs with the included keystore
- Uploads artifacts with commit hash naming
- Supports manual workflow dispatch

## 📦 Project Structure

```
app/
├── src/main/
│   ├── java/com/notex/create/
│   │   ├── MainActivity.java          # Main notes listing
│   │   ├── EditActivity.java          # Note editing
│   │   ├── ViewActivity.java          # Note viewing
│   │   ├── SettingsActivity.java      # App settings
│   │   └── OptionsBottomSheet.java    # Context menu
│   └── res/
│       ├── layout/                    # Material 3 layouts
│       ├── values/                    # Colors, styles, themes
│       └── drawable/                  # Icons and backgrounds
├── build.gradle                       # App build configuration
└── NoteX.keystore                    # Release signing key
```

## 🎨 Design System

### Color Palette
- **Primary**: Purple tones for main actions and branding
- **Secondary**: Complementary colors for secondary actions
- **Surface**: Clean backgrounds with proper contrast
- **Error**: Clear error states with appropriate colors

### Typography
- **Headlines**: Sans-serif medium for titles and headers
- **Body**: Sans-serif regular for content and descriptions
- **Labels**: Sans-serif medium for buttons and labels

### Components
- **Cards**: Elevated cards with 12dp corner radius
- **Buttons**: Text buttons with proper touch targets
- **Text Fields**: Outlined style with Material 3 theming
- **Bottom Sheets**: Rounded corners with drag handles

## 🔒 Privacy & Security
- **Offline-Only**: All data stays on your device
- **No Tracking**: No analytics or data collection
- **Local Storage**: Uses Android's SharedPreferences for data persistence
- **Secure**: No network permissions or external dependencies

## 📄 License
This project is open source and available under the MIT License.

## 🤝 Contributing
Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## 🔄 Version History
- **v1.0.0**: Initial release with Material 3 design and core functionality

---

Built with ❤️ using Material 3 and modern Android development practices.