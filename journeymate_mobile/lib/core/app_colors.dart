import 'package:flutter/material.dart';

class AppColors {
  static const teal950 = Color(0xFF042F2E);
  static const teal900 = Color(0xFF134E4A);
  static const teal800 = Color(0xFF115E59);
  static const teal700 = Color(0xFF0F766E);
  static const teal600 = Color(0xFF0D9488);
  static const teal500 = Color(0xFF14B8A6);
  static const teal400 = Color(0xFF2DD4BF);
  static const teal300 = Color(0xFF5EEAD4);
  static const teal200 = Color(0xFF99F6E4);
  static const teal100 = Color(0xFFCCFBF1);
  static const teal50  = Color(0xFFF0FDFA);
  static const lime    = Color(0xFFE9FC9E);

  static const white70 = Color(0xB3FFFFFF);
  static const white40 = Color(0x66FFFFFF);
  static const white20 = Color(0x33FFFFFF);

  static const gradientMain = LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 0.5, 1.0],
    colors: [Color(0xFF1CB5B0), Color(0xFFE9FC9E), Color(0xFF1CB5B0)],
  );

  static const gradientDark = LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    stops: [0.0, 0.5, 1.0],
    colors: [Color(0xFF0D4F4C), Color(0xFF1CB5B0), Color(0xFFE9FC9E)],
  );

  static const gradientHome = LinearGradient(
    begin: Alignment(−0.5, −1.0),
    end: Alignment(0.5, 1.0),
    stops: [0.0, 0.4, 0.75, 1.0],
    colors: [
      Color(0xFF0D4F4C),
      Color(0xFF1CB5B0),
      Color(0xFF3D7A6E),
      Color(0xFF0D4F4C),
    ],
  );
}

class AppTextStyles {
  static const headline = TextStyle(
    fontWeight: FontWeight.w900,
    letterSpacing: -1.0,
    color: AppColors.teal900,
  );

  static const label = TextStyle(
    fontSize: 9,
    fontWeight: FontWeight.w900,
    letterSpacing: 3.0,
    color: AppColors.teal900,
  );

  static const badge = TextStyle(
    fontSize: 10,
    fontWeight: FontWeight.w900,
    letterSpacing: 2.0,
    color: Colors.white,
  );
}