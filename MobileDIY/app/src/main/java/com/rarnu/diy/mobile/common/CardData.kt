package com.rarnu.diy.mobile.common

import com.isyscore.kotlin.common.json.JSONObject
import java.io.Serializable

data class CardData(
    var language: String = "jp",
    var name: String = "[青眼の白龍(ブルーアイズ・ホワイト・ドラゴン)]",
    var color: String = "",
    var type: String = "monster",
    var attribute: String = "light",
    var icon: String = "",
    var image: String = "",
    var cardType: String = "normal",
    var pendulumType: String = "normal-pendulum",
    var level: Int = 8,
    var rank: Int = 0,
    var pendulumScale: Int = 0,
    var pendulumDescription: String = "",
    var monsterType: String = "ドラゴン[族(ぞく)]／[通(つう)][常(じょう)]",
    var atk: Int = 3000,
    var def: Int = 2500,
    var arrowList: List<Int> = listOf(),
    var description: String = "[高(たか)]い[攻(こう)][撃(げき)][力(りょく)]を[誇(ほこ)]る[伝(でん)][説(せつ)]のドラゴン。どんな[相(あい)][手(て)]でも[粉(ふん)][砕(さい)]する、その[破(は)][壊(かい)][力(りょく)]は[計(はか)]り[知(し)]れない。",
    var `package`: String = "SD25-JP001",
    var password: String = "89631139",
    var copyright: String = "jp",
    var laser: Boolean = true,
    var radius: Boolean = true,
    var cardBack: Boolean = false,
    var scale: Double = 0.30,
    var firstLineCompress: Boolean = false,
    var flash0: Boolean = false,
    var flash1: Boolean = false,
    var flash2: Int = 0,
    var redName: String = ""
) : Serializable {

    companion object {
        fun fromJson(jsonStr: String): CardData? = try {
            val json = JSONObject(jsonStr)
            CardData(
                language = json.getString("language"),
                name = json.getString("name"),
                color = json.getString("color"),
                type = json.getString("type"),
                attribute = json.getString("attribute"),
                icon = json.getString("icon"),
                image = json.getString("image"),
                cardType = json.getString("cardType"),
                pendulumType = json.getString("pendulumType"),
                level = json.getInt("level"),
                rank = json.getInt("rank"),
                pendulumScale = json.getInt("pendulumScale"),
                pendulumDescription = json.getString("pendulumDescription"),
                monsterType = json.getString("monsterType"),
                atk = json.getInt("atk"),
                def = json.getInt("def"),
                arrowList = try {
                    json.getJSONObject("arrowList").run { keySet().map { k -> getInt(k) } }
                } catch (e: Exception) {
                    json.getJSONArray("arrowList").map { it as Int }
                },
                description = json.getString("description"),
                `package` = json.getString("package"),
                password = json.getString("password"),
                copyright = json.getString("copyright"),
                laser = json.getBoolean("laser"),
                radius = json.getBoolean("radius"),
                cardBack = json.getBoolean("cardBack"),
                scale = json.getDouble("scale"),
                firstLineCompress = json.getBoolean("firstLineCompress"),
                flash0 = json.getBoolean("flash0"),
                flash1 = json.getBoolean("flash1"),
                flash2 = json.getInt("flash2"),
                redName = json.getString("redName")
            )
        } catch (th: Throwable) {
            null
        }
    }

    fun toJsObj(): String {
        var str = "language: '${language}',"
        str += "name: '${name}',"
        str += "color: '${color}',"
        str += "type: '${type}',"
        str += "attribute: '${attribute}',"
        str += "icon: '${icon}',"
        str += "cardType: '${cardType}',"
        str += "pendulumType: '${pendulumType}',"
        str += "level: ${level},"
        str += "rank: ${rank},"
        str += "pendulumScale: ${pendulumScale},"
        str += "pendulumDescription: '${pendulumDescription.replace("\n", "\\n")}',"
        str += "monsterType: '${monsterType}',"
        str += "atk: ${atk},"
        str += "def: ${def},"
        str += "arrowList: [${arrowList.joinToString(",")}],"
        str += "description: '${description.replace("\n", "\\n")}',"
        str += "package: '${`package`}',"
        str += "password: '${password}',"
        str += "copyright: '${copyright}',"
        str += "laser: ${laser},"
        str += "radius: ${radius},"
        str += "cardBack: ${cardBack},"
        str += "firstLineCompress: ${firstLineCompress},"
        str += "flash0: $flash0,"
        str += "flash1: $flash1,"
        str += "flash2: $flash2,"
        str += "redName: '${redName}',"
        str += "image: '${image}'"
        return "{${str}}"
    }

}