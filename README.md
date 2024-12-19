ChooseTedBottomPicker

A feature-rich Android library that simplifies selecting images or files through a customizable bottom sheet interface. Built with Kotlin, AndroidX, and modern tools, ChooseTedBottomPicker is ideal for applications that require smooth and intuitive user interactions for media selection.

Features

Elegant and customizable bottom sheet picker.

Multiple image selection support.

Built-in permissions handling.

Compatible with modern Android development tools (Jetpack Compose, ViewBinding, etc.).

Lightweight and easy to integrate.

Installation

Gradle Dependency

Add the following dependency to your project-level build.gradle:

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

Add the dependency to your app-level build.gradle:

implementation 'io.github.ParkSangGwon:tedimagepicker:1.6.1'
implementation 'io.github.ParkSangGwon:tedpermission-rx2:3.3.0'

Usage

1. Initialize TedBottomPicker

Use the following snippet to invoke the bottom sheet image picker:

import gun0912.tedimagepicker.builder.TedImagePicker

fun showImagePicker(context: Context) {
    TedImagePicker.with(context)
        .title("Choose an Image")
        .showCameraTile(false)
        .dropDownAlbumName(false)
        .start { uri ->
            // Handle selected image URI
            displaySelectedImage(uri)
        }
}

private fun displaySelectedImage(uri: Uri) {
    imageView.setImageURI(uri) // Example usage
}

2. Permissions Handling

The library handles permissions automatically for selecting images or accessing the camera. However, for advanced permission management, you can use:

TedPermission.with(context)
    .setPermissionListener(object : PermissionListener {
        override fun onPermissionGranted() {
            showImagePicker(context)
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    })
    .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    .check()

Customization

TedBottomPicker offers various options for customization, including:

Camera Tile: Show or hide the camera tile using .showCameraTile(boolean).

Title: Customize the picker title with .title(String).

Album Dropdown: Enable or disable album dropdown using .dropDownAlbumName(boolean).

Image Count: Limit the number of selectable images using .max(Count).

For more advanced options, refer to the official documentation.

Screenshots

Single Selection

Multi Selection





Requirements

Minimum SDK: 21 (Android 5.0 Lollipop)

Target SDK: 34

Languages: Kotlin, Java

Contributing

We welcome contributions! Please follow these steps:

Fork the repository.

Create a new branch for your feature or bug fix.

Submit a pull request with detailed description of changes.

License

This library is licensed under the Apache 2.0 License.

Acknowledgments

This library is built on top of:

TedImagePicker

TedPermission

Contact

For support or inquiries, feel free to open an issue or reach out to
