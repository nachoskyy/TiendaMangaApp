package com.example.tiendamangaapp.core

import com.example.tiendamangaapp.data.local.AppDatabase
import com.example.tiendamangaapp.data.local.Manga

object Bootstrap {
    suspend fun ensureSeeded(db: AppDatabase) {
        val dao = db.mangaDao()
        if (dao.getAll().isEmpty()) {
            dao.insertAll(
                listOf(
                    Manga(titulo="One Piece #1", autor="Eiichiro Oda", genero="Aventura", precio=9900, stock=12, sinopsis="Sigue las aventuras de Monkey D. Luffy un chico que sueña con ser el rey de los piratas.", portadaUrl="https://images.cdn3.buscalibre.com/fit-in/360x360/b1/0c/b10c11bb8e79371aca5501de04da669f.jpg"),
                    Manga(titulo="Jujutsu Kaisen #0", autor="Gege Akutami", genero="Accion", precio=12990, stock=10, sinopsis="PRECUELA DEL POPULAR MANGA JUJUTSU KAISEN ; Sigue la historia de Yuta Okkotsu un estudiante que estaba sufriendo una aparicion vengativa de su amada fallecida.", portadaUrl="https://m.media-amazon.com/images/I/71B8SNoCpYL._AC_UF1000,1000_QL80_.jpg"),
                    Manga(titulo="Sakamoto Days #6", autor="Yuto Suzuki", genero="Accion y Comedia", precio=10990, stock=8, sinopsis="Comienza una guerra despiadada entre los condenados a muerte de alto nivel de peligrosidad, la Orden y la Tienda Sakamoto!", portadaUrl="https://cdn.kobo.com/book-images/2177dbea-29df-4e82-b99d-12b5bbaedacc/353/569/90/False/sakamoto-days-vol-6.jpg"),
                    Manga(titulo="Kagurabachi #7", autor="Takeru Hokazono", genero="Shonen", precio=12500, stock=7, sinopsis="Chihiro, un joven con aspiraciones de convertirse en forjador de espadas, entrena todos los dias bajo la tutela de su padre, un forjador de gran renombre. Un dia la tragedia irrumpe en sus vidas, impulsando a Chihiro a empuñar su espada en busca de venganza.", portadaUrl="https://d3tvwjfge35btc.cloudfront.net/Assets/43/278/L_p0220227843.jpg")
                )
            )
        }
    }
}
