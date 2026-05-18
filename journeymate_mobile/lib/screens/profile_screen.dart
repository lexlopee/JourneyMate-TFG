import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';

import '../core/app_colors.dart';
import '../services/auth_service.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});
  @override State<ProfileScreen> createState() => _S();
}

class _S extends State<ProfileScreen> {
  String _name = ''; bool _logged = false;

  @override void initState() { super.initState(); _load(); }

  Future<void> _load() async {
    final ok = await AuthService.isLoggedIn();
    final n  = await AuthService.getUserName();
    if (mounted) setState(() { _logged = ok; _name = n ?? ''; });
  }

  Future<void> _logout() async {
    final ok = await showModalBottomSheet<bool>(
      context: context, backgroundColor: Colors.transparent,
      builder: (_) => Container(
        margin: const EdgeInsets.all(12),
        decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(28)),
        padding: const EdgeInsets.fromLTRB(24,20,24,32),
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          Container(width:40,height:4,margin:const EdgeInsets.only(bottom:20),
              decoration:BoxDecoration(color:Colors.grey[300],borderRadius:BorderRadius.circular(2))),
          const Icon(LucideIcons.logOut, size:40, color:Colors.redAccent),
          const SizedBox(height:12),
          const Text('Cerrar sesión',style:TextStyle(fontSize:18,fontWeight:FontWeight.w900,color:AppColors.teal900)),
          const SizedBox(height:6),
          const Text('¿Seguro que quieres salir?',style:TextStyle(color:Colors.grey,fontSize:13)),
          const SizedBox(height:24),
          Row(children:[
            Expanded(child:OutlinedButton(onPressed:()=>Navigator.pop(context,false),
                style:OutlinedButton.styleFrom(padding:const EdgeInsets.symmetric(vertical:14),
                    shape:RoundedRectangleBorder(borderRadius:BorderRadius.circular(14))),
                child:const Text('Cancelar',style:TextStyle(fontWeight:FontWeight.w700)))),
            const SizedBox(width:12),
            Expanded(child:ElevatedButton(onPressed:()=>Navigator.pop(context,true),
                style:ElevatedButton.styleFrom(backgroundColor:Colors.redAccent,foregroundColor:Colors.white,
                    padding:const EdgeInsets.symmetric(vertical:14),minimumSize:const Size(0,0),
                    shape:RoundedRectangleBorder(borderRadius:BorderRadius.circular(14))),
                child:const Text('Salir',style:TextStyle(fontWeight:FontWeight.w900)))),
          ]),
        ]),
      ),
    );
    if (ok == true) {
      await AuthService.logout();
      if (mounted) setState(() { _logged = false; _name = ''; });
    }
  }

  void _sobreJourneyMate() {
    showModalBottomSheet(
      context: context, backgroundColor: Colors.transparent,
      isScrollControlled: true,
      builder: (_) => Container(
        margin: const EdgeInsets.all(12),
        decoration: BoxDecoration(color:Colors.white,borderRadius:BorderRadius.circular(28)),
        padding: const EdgeInsets.fromLTRB(24,16,24,40),
        child: Column(mainAxisSize:MainAxisSize.min, children:[
          Container(width:40,height:4,margin:const EdgeInsets.only(bottom:20),
              decoration:BoxDecoration(color:Colors.grey[300],borderRadius:BorderRadius.circular(2))),
          Container(width:72,height:72,
              decoration:BoxDecoration(
                  gradient:const LinearGradient(colors:[AppColors.teal400,AppColors.teal700],
                      begin:Alignment.topLeft,end:Alignment.bottomRight),
                  borderRadius:BorderRadius.circular(22),
                  boxShadow:[BoxShadow(color:AppColors.teal600.withOpacity(0.3),blurRadius:16,offset:const Offset(0,6))]),
              child:const Icon(LucideIcons.globe,size:34,color:Colors.white)),
          const SizedBox(height:14),
          const Text('JourneyMate',style:TextStyle(fontSize:24,fontWeight:FontWeight.w900,color:AppColors.teal900,letterSpacing:-0.5)),
          const SizedBox(height:4),
          const Text('Tu compañero de viaje inteligente',style:TextStyle(fontSize:13,color:Colors.grey,fontWeight:FontWeight.w600)),
          const SizedBox(height:6),
          Container(padding:const EdgeInsets.symmetric(horizontal:12,vertical:4),
              decoration:BoxDecoration(color:AppColors.teal50,borderRadius:BorderRadius.circular(8)),
              child:const Text('Versión 1.0.0',style:TextStyle(fontSize:11,color:AppColors.teal600,fontWeight:FontWeight.w900))),
          const SizedBox(height:20),
          Container(
              padding:const EdgeInsets.all(16),
              decoration:BoxDecoration(color:const Color(0xFFF9FAFB),borderRadius:BorderRadius.circular(16)),
              child:const Text(
                  'JourneyMate es una plataforma integral de viajes que te permite buscar y reservar vuelos, hoteles, coches, cruceros y actividades en todo el mundo.\n\nDiseñada para hacer tu experiencia de viaje más fácil, intuitiva y personalizada.',
                  style:TextStyle(fontSize:13,color:AppColors.teal900,height:1.6),textAlign:TextAlign.center)),
          const SizedBox(height:16),
          const Text('Equipo de desarrollo',style:TextStyle(fontSize:12,fontWeight:FontWeight.w900,color:Colors.grey,letterSpacing:1)),
          const SizedBox(height:10),
          _member('Daniel Fernández','Desarrollador Full Stack'),
          const SizedBox(height:6),
          _member('Alejandro López','Desarrollador Full Stack'),
          const SizedBox(height:6),
          _member('M. Hernan Martín','Desarrollador Full Stack'),
          const SizedBox(height:20),
          Wrap(spacing:8,runSpacing:8,alignment:WrapAlignment.center,children:[
            _badge('Flutter',LucideIcons.smartphone),
            _badge('Spring Boot',LucideIcons.server),
            _badge('PostgreSQL',LucideIcons.database),
            _badge('Google Cloud',LucideIcons.cloud),
            _badge('Stripe',LucideIcons.creditCard),
            _badge('PayPal',LucideIcons.wallet),
          ]),
          const SizedBox(height:20),
          SizedBox(width:double.infinity,child:ElevatedButton(
              onPressed:()=>Navigator.pop(context),
              style:ElevatedButton.styleFrom(backgroundColor:AppColors.teal600,foregroundColor:Colors.white,
                  padding:const EdgeInsets.symmetric(vertical:14),
                  shape:RoundedRectangleBorder(borderRadius:BorderRadius.circular(16))),
              child:const Text('Cerrar',style:TextStyle(fontWeight:FontWeight.w900)))),
        ]),
      ),
    );
  }

  Widget _member(String name, String role) => Container(
      padding:const EdgeInsets.symmetric(horizontal:14,vertical:10),
      decoration:BoxDecoration(color:AppColors.teal50,borderRadius:BorderRadius.circular(12)),
      child:Row(children:[
        Container(width:34,height:34,decoration:const BoxDecoration(color:AppColors.teal600,shape:BoxShape.circle),
            child:Center(child:Text(name[0],style:const TextStyle(color:Colors.white,fontWeight:FontWeight.w900,fontSize:14)))),
        const SizedBox(width:10),
        Expanded(child:Column(crossAxisAlignment:CrossAxisAlignment.start,children:[
          Text(name,style:const TextStyle(fontWeight:FontWeight.w900,fontSize:13,color:AppColors.teal900)),
          Text(role,style:const TextStyle(fontSize:11,color:Colors.grey,fontWeight:FontWeight.w600)),
        ])),
      ]));

  Widget _badge(String label, IconData icon) => Container(
      padding:const EdgeInsets.symmetric(horizontal:10,vertical:6),
      decoration:BoxDecoration(color:AppColors.teal50,borderRadius:BorderRadius.circular(10),
          border:Border.all(color:AppColors.teal100)),
      child:Row(mainAxisSize:MainAxisSize.min,children:[
        Icon(icon,size:12,color:AppColors.teal600),const SizedBox(width:4),
        Text(label,style:const TextStyle(fontSize:11,fontWeight:FontWeight.w900,color:AppColors.teal700)),
      ]));

  void _prox(String n) => ScaffoldMessenger.of(context).showSnackBar(SnackBar(
      content:Text('$n — próximamente 🚀',style:const TextStyle(fontWeight:FontWeight.w700)),
      backgroundColor:AppColors.teal700,behavior:SnackBarBehavior.floating,
      shape:RoundedRectangleBorder(borderRadius:BorderRadius.circular(16)),
      margin:const EdgeInsets.all(16),duration:const Duration(seconds:2)));

  @override
  Widget build(BuildContext context) => Scaffold(
    backgroundColor:Colors.transparent,
    body:Container(
      decoration:const BoxDecoration(gradient:AppColors.gradientMain),
      child:SafeArea(child:CustomScrollView(physics:const BouncingScrollPhysics(),slivers:[
        SliverToBoxAdapter(child:_header()),
        SliverToBoxAdapter(child:_menu()),
        const SliverToBoxAdapter(child:SizedBox(height:100)),
      ])),
    ),
  );

  Widget _header() => Padding(
      padding:const EdgeInsets.fromLTRB(20,20,20,0),
      child:Column(children:[
        Container(width:88,height:88,
            decoration:BoxDecoration(
                color:_logged?AppColors.teal700:Colors.white.withOpacity(0.2),shape:BoxShape.circle,
                border:Border.all(color:Colors.white.withOpacity(0.4),width:3),
                boxShadow:[BoxShadow(color:Colors.black.withOpacity(0.1),blurRadius:12)]),
            child:_logged&&_name.isNotEmpty
                ?Center(child:Text(_name[0].toUpperCase(),style:const TextStyle(color:Colors.white,fontWeight:FontWeight.w900,fontSize:34)))
                :const Icon(LucideIcons.user,color:Colors.white,size:40)),
        const SizedBox(height:14),
        Text(_logged?_name:'Invitado',style:const TextStyle(color:Colors.white,fontWeight:FontWeight.w900,fontSize:22,letterSpacing:-0.3)),
        const SizedBox(height:4),
        Text(_logged?'Viajero JourneyMate':'Inicia sesión para ver tus reservas',
            style:const TextStyle(color:Colors.white60,fontSize:12)),
        const SizedBox(height:20),
        if (!_logged) Row(children:[
          Expanded(child:ElevatedButton(onPressed:()=>context.go('/login'),
              style:ElevatedButton.styleFrom(backgroundColor:Colors.white,foregroundColor:AppColors.teal900,
                  minimumSize:const Size(0,48),shape:RoundedRectangleBorder(borderRadius:BorderRadius.circular(16))),
              child:const Text('INICIAR SESIÓN',style:TextStyle(fontWeight:FontWeight.w900,fontSize:12,letterSpacing:1.5)))),
          const SizedBox(width:10),
          Expanded(child:OutlinedButton(onPressed:()=>context.go('/register'),
              style:OutlinedButton.styleFrom(foregroundColor:Colors.white,side:const BorderSide(color:Colors.white38),
                  minimumSize:const Size(0,48),shape:RoundedRectangleBorder(borderRadius:BorderRadius.circular(16))),
              child:const Text('REGISTRO',style:TextStyle(fontWeight:FontWeight.w900,fontSize:12,letterSpacing:1.5)))),
        ]),
      ]));

  Widget _menu() => Padding(
      padding:const EdgeInsets.fromLTRB(16,20,16,0),
      child:Container(
          decoration:BoxDecoration(color:Colors.white,borderRadius:BorderRadius.circular(24),
              boxShadow:[BoxShadow(color:Colors.black.withOpacity(0.06),blurRadius:12)]),
          child:Column(children:[
            if (_logged)...[
              _tile(LucideIcons.bookOpen,'Mis Reservas',AppColors.teal600,()=>context.go('/mis-reservas')),
              const _D(),
            ],
            _tile(LucideIcons.globe,'Idioma',AppColors.teal500,()=>_prox('Idioma'),soon:true),const _D(),
            _tile(LucideIcons.shieldCheck,'Privacidad',AppColors.teal500,()=>_prox('Privacidad'),soon:true),const _D(),
            _tile(LucideIcons.info,'Ayuda',AppColors.teal500,()=>_prox('Ayuda'),soon:true),const _D(),
            _tile(LucideIcons.info,'Sobre JourneyMate',AppColors.teal600,_sobreJourneyMate),
            if (_logged)...[
              const Divider(height:1,indent:16,endIndent:16),
              _tile(LucideIcons.logOut,'Cerrar sesión',Colors.redAccent,_logout,
                  textColor:Colors.redAccent,arrow:false),
            ],
          ])));

  Widget _tile(IconData icon,String label,Color color,VoidCallback onTap,
      {Color? textColor,bool arrow=true,bool soon=false}) => ListTile(
      onTap:onTap,
      contentPadding:const EdgeInsets.symmetric(horizontal:16,vertical:2),
      leading:Container(width:38,height:38,
          decoration:BoxDecoration(color:color.withOpacity(soon?0.06:0.1),borderRadius:BorderRadius.circular(10)),
          child:Icon(icon,size:18,color:soon?color.withOpacity(0.4):color)),
      title:Text(label,style:TextStyle(fontWeight:FontWeight.w700,fontSize:14,
          color:soon?Colors.grey.withOpacity(0.5):(textColor??AppColors.teal900))),
      trailing:soon
          ?Container(padding:const EdgeInsets.symmetric(horizontal:7,vertical:2),
          decoration:BoxDecoration(color:const Color(0xFFF3F4F6),borderRadius:BorderRadius.circular(6)),
          child:const Text('Próx.',style:TextStyle(fontSize:9,fontWeight:FontWeight.w900,color:Colors.grey)))
          :arrow?const Icon(LucideIcons.chevronRight,size:16,color:Colors.grey):null);
}

class _D extends StatelessWidget {
  const _D();
  @override Widget build(BuildContext c)=>const Divider(height:1,indent:16,endIndent:16,color:Color(0xFFF3F4F6));
}