package vlprojects.vndb

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import vlprojects.vndb.parameters.Options
import vlprojects.vndb.parameters.eq
import vlprojects.vndb.result.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VNDBTest {
    private lateinit var vndb: VNDBService

    @Test
    @BeforeAll
    fun loginTest() = runBlocking {
        vndb = VNDBService()
        val result = vndb.login()
        assert(result is Result.Success)
    }

    @Test
    fun dbstatsTest() = runBlocking {
        val result = vndb.getDBStats()
        assert(result is Result.Success<DBStats>)
        val stats = (result as Result.Success<DBStats>).data
        assert(stats.vn > 0)
    }

    @Test
    fun ever17Test() = runBlocking {
        val results = vndb.getVN(filter = "id" eq 17)
        assert(results is Result.Success<GetResults<VNBasic>>)
        results as Result.Success<GetResults<VNBasic>>
        val data = results.data

        assertEquals(data.num, 1)
        assertEquals(data.items.size, 1)

        val ever17 = data.items.single()
        assertEquals(ever17.id, 17)
        assertEquals(ever17.origLanguage.single(), "ja")
    }

    @Test
    fun quoteTest() = runBlocking {
        val result = vndb.getQuote()
        assert(result is Result.Success)
        result as Result.Success<GetResults<Quote>>
        assert(result.data.num == 1)
        val quote = result.data.items.single()
        assert(quote.id > 0)
        println(quote)
    }

    @Test
    fun parseMixedArray() = runBlocking {
        val result = vndb.getCharacter(filter = "vn" eq 2002, options = Options(results = 4))
        result as Result.Success
        val items = result.data.items
        val item = items.first()
        assertNotNull(item.vns)

        assertNotNull(item.getVNSpoilerLevel().first(), "おはよう、お兄ちゃん")
    }

    @Test
    fun optionsTest() {
        val o1 = Options()
        assertEquals(o1.toString(), "{}")

        val resultCount = 20
        val o2 = Options(results = resultCount)
        assertEquals(o2.toString(), """{"results":$resultCount}""")


        val o3 = Options()
        assertEquals(o3.toString(), "{}")

        val o4 = Options()
        assertEquals(o4.toString(), "{}")
    }

    @Test
    fun lotsOfVns() = runBlocking {
//        val resp = vndb.sendGetCommandWithResponse("vn", arrayOf("basic"), filter = "(title~\"ever\")", options = parameters.Options(results = 15).toString())
        val resp = Response("results", """{"items":[
            |{"title":"Ever17 -The Out of Infinity-","id":17,"languages":["en","es","ja","ru","zh"],"orig_lang":["ja"],"original":null,"platforms":["win","ios","and","psp","ps2","drc","vnd"],"released":"2002-08-29"},
            |{"original":null,"orig_lang":["ja"],"released":"2000-03-23","platforms":["win","lin","mac","ios","and","psp","ps1","ps2","drc","vnd"],"id":248,"languages":["en","ja","ru","zh"],"title":"Never7 -The End of Infinity-"},
            |{"languages":["en","ja","ru"],"id":287,"title":"The World to Reverse.","original":null,"orig_lang":["ja"],"platforms":["win","lin","mac","vnd"],"released":"2006-03-31"},
            |{"languages":["de","en","es","fr","pl","pt-br","ru","vi"],"id":870,"title":"The Best Eroge Ever","original":null,"orig_lang":["en"],"released":"2007-11-04","platforms":["win","lin","mac","and","psp","drc","vnd"]},
            |{"orig_lang":["ja"],"original":"??????????3?Sweet Songs Forever?","released":"2000-12-08","platforms":["win"],"title":"Triangle Heart 3 ~Sweet Songs Forever~","languages":["ja"],"id":1122},
            |{"released":"2004-09-17","platforms":["win"],"orig_lang":["ja"],"original":null,"title":"Abandoner - The Severed Dreams","languages":["ja"],"id":1182},
            |{"orig_lang":["ja"],"original":null,"released":"2002-07-26","platforms":["win","dvd"],"title":"Sultan ~The Lovesong is Forever~","id":1350,"languages":["ja"]},
            |{"orig_lang":["ja"],"original":"??????","released":"2007-07-20","platforms":["win"],"title":"Reversible","id":1416,"languages":["ja"]},
            |{"orig_lang":["ja"],"original":"???×????Lovevery Sisters?","released":"2009-04-24","platforms":["win","dvd"],"title":"Sister x Sister ~Lovevery Sisters~","id":1778,"languages":["ja"]},
            |{"orig_lang":["ja"],"original":"????????","platforms":["win"],"released":"2000-04-07","title":"Clever Magic","languages":["ja"],"id":2174},
            |{"title":"Kikouyoku Senki Tenkuu no Yumina FD  -ForeverDreams-","id":2886,"languages":["ja"],"platforms":["win"],"released":"2010-01-29","orig_lang":["ja"],"original":"????? ??????FD -ForeverDreams-"},
            |{"platforms":["win","lin","mac","and"],"released":"2009-08-26","original":null,"orig_lang":["en"],"id":3367,"languages":["en","es","ru"],"title":"Nevermore"},
            |{"platforms":["win","lin","mac","and"],"released":"2010-01-16","original":null,"orig_lang":["ru"],"languages":["de","en","ru"],"id":3452,"title":"UVAO Forever"},
            |{"id":3794,"languages":["ja"],"title":"Ever17 CrossOver Impression","platforms":["win"],"released":"2005-12-30","original":null,"orig_lang":["ja"]},
            |{"title":"Reverse Desire ~Uragaeru Yokubou~","id":4174,"languages":["ja"],"platforms":["win","dvd"],"released":"2004-02-27","orig_lang":["ja"],"original":"Reverse desire???????"}
            |],"more":true,"num":15}""".trimMargin())
        val parse = Gson().fromJson<GetResults<VNBasic>>(resp.json, object : TypeToken<GetResults<VNBasic>>(){}.type)
        val list = parse.items
    }

    @Test
    fun responses() /*= runBlocking*/ {
        val resp = Response("results", """{"items":[
            |{"bloodt":"a","id":6487,"gender":"f","age":18,"birthday":[25,7],"aliases":"Christina\nChris\nAssistant\nCeleb 17\nThe Zombie\nPerverted Genius Girl\nKu-nyan","name":"Makise Kurisu","vns":[[2002,0,0,"primary"],[5883,0,1,"appears"],[6618,0,0,"primary"],[9887,0,0,"primary"],[11660,0,0,"primary"],[17102,0,0,"appears"],[21281,0,0,"appears"],[23221,0,0,"primary"]],"original":"?? ???","image":"https://s2.vndb.org/ch/34/97234.jpg","image_flagging":{"violence_avg":0,"sexual_avg":0,"votecount":11},"description":"Kurisu is a neuroscience researcher at an American university, and can speak and read English well. Having had her research published in the academic journal Science at the young age of 18, Makise is extremely talented. She skipped a grade in the American school system. She does not get along with her father and has not spoken to him in many years. Okabe often just calls her assistant or one of a number of nicknames he comes up with, such as \"Christina\", \"the Zombie\", and \"Celeb Sev\", none of which she likes. She is considered a tsundere character; she dislikes this characterization as well, strongly objecting whenever Daru mentions it.","spoil_gender":null},
            |{"bloodt":"o","id":6491,"gender":"f","age":16,"aliases":"Mayushii\nMayushii NyanNyan","birthday":[1,2],"vns":[[2002,0,0,"primary"],[6618,0,0,"primary"],[9887,0,0,"primary"],[11660,0,0,"primary"],[21281,0,0,"appears"],[23221,0,0,"primary"]],"name":"Shiina Mayuri","image_flagging":{"sexual_avg":0,"violence_avg":0,"votecount":11},"original":"?? ???","image":"https://s2.vndb.org/ch/95/97195.jpg","description":"Mayuri is a long-time childhood friend of [url=/c6498]Okabe[/url] and a bit of an airhead. Mayuri enjoys creating cosplay costumes and has a part-time job at a maid cafe called \"Mayqueen Nyannyan\". She often calls herself Mayushii, a portmanteau of her given name and surname, which is also what [url=/c6493]Daru[/url] calls her. She has a distinctive sing-song way of speaking, and she typically sings \"tutturuu\" when she arrives or introduces herself.","spoil_gender":null},
            |{"bloodt":"o","gender":"f","id":6492,"age":18,"birthday":[27,9],"aliases":"Working Warrior\n?????\nPart-Time Warrior\nAlpha Suzuha","vns":[[2002,0,0,"primary"],[6618,0,0,"primary"],[9887,0,0,"side"],[11660,0,0,"primary"],[17102,0,0,"appears"],[23221,0,0,"primary"]],"name":"Amane Suzuha","image":"https://s2.vndb.org/ch/36/97236.jpg","original":"??? ??","image_flagging":{"violence_avg":0,"sexual_avg":0,"votecount":10},"description":"Amane Suzuha works part-time for the landlord of Rintarou's flat and is on a search for her father in Akihabara. She enjoys riding her bicycle and appears to be at odds with Kurisu.\n\n[spoiler]She is in fact a Time Traveller from 2036 who, using the guise of John Titor, seeks to prevent SERN from creating a Time Machine that will lead to dystopia. Though she is unaware of it, she is the daughter of [url=/c6493]Hashida Itaru[/url].[/spoiler]\n\nFrom [url=http://steins-gate.wikia.com/wiki/Suzuha_Amane]Wiki[/url]","spoil_gender":null},
            |{"image_flagging":{"violence_avg":0,"sexual_avg":0,"votecount":11},"original":"?? ?","image":"https://s2.vndb.org/ch/38/97238.jpg","description":"Daru is an experienced hacker who has known Okabe since high school. He is very skilled at computer programming and with old and new computer hardware. He is also well-versed in things pertaining to otaku culture. Okabe and Shiina refer to him by the nickname \"Daru\", a portmanteau of his given name and surname. Okabe sometimes also calls him Super Haker, mispronouncing the word \"hacker\" to his chagrin. He is a big fan of Faris and he frequently says things which could be taken as sexual harassment. Daru is often annoyed with Okabe's frequent delusional behavior.","spoil_gender":null,"aliases":"Daru\nDaSH\nDaSP\nSupah Hakah","birthday":[19,5],"vns":[[2002,0,0,"primary"],[5883,0,1,"appears"],[6618,0,0,"side"],[9887,0,0,"primary"],[11660,0,0,"primary"],[17102,0,0,"primary"],[21281,0,0,"main"],[23221,0,0,"primary"]],"name":"Hashida Itaru","gender":"m","id":6493,"age":19,"bloodt":"b"},
            |{"name":"Akiha Rumiho","vns":[[2002,0,0,"primary"],[6618,0,0,"primary"],[9887,0,0,"side"],[11660,0,0,"primary"],[23221,0,0,"primary"]],"birthday":[3,4],"aliases":"Faris NyanNyan\nFeris","description":"Faris Nyannyan works at the maid cafe \"Mayqueen Nyannyan\", the same maid cafe that Shiina works at, and is the most popular waitress there. Her real name is Akiha Rumiho, whose family owns Akihabara, her being the one suggested it become the city of moe and anime. She tends to add \"nyan\" to her sentences.","spoil_gender":null,"original":"?? ???","image":"https://s2.vndb.org/ch/35/97235.jpg","image_flagging":{"votecount":13,"violence_avg":0,"sexual_avg":0},"bloodt":"ab","age":17,"gender":"f","id":6494},
            |{"image_flagging":{"votecount":11,"sexual_avg":0,"violence_avg":0},"image":"https://s2.vndb.org/ch/46/28046.jpg","original":"??? ??","spoil_gender":null,"description":"He is Okabe's landlord, who owns a TV repair store beneath his apartment, living with his daughter, Nae. Okabe often gives him the nickname \"Mr. Braun\" for his passion to Braun tube TV.\n[spoiler]After seeing a gel-i-fied human in the sewers he was residing in, he was recruited by SERN, which provided the money he needed to get out of the slums. After a while SERN ordered him to go to Akihabara in Japan. It was here that he met Suzuha Hashida, his neighbour, and Tsuzuri Imamiya, his future wife.\nSometime prior to the first D-Mail, Tennouji recruited Moeka Kiryuu into the Rounders and communicated with her as \"FB\". It was only until Rintarou and Moeka discovers his identity was FB revealed to be a man, as FB introduced themselves as a woman to all Rounders recruits.[/spoiler]","aliases":"Mr. Braun","birthday":[12,3],"name":"Tennouji Yuugo","vns":[[2002,0,0,"side"],[6618,0,0,"side"],[9887,0,0,"appears"],[11660,0,0,"primary"],[17102,0,0,"side"],[23221,0,0,"side"]],"gender":"m","id":6495,"age":32,"bloodt":"o"},
            |{"id":6496,"gender":"f","age":20,"bloodt":"b","image_flagging":{"votecount":13,"violence_avg":0,"sexual_avg":0},"original":"?? ??","image":"https://s2.vndb.org/ch/38/28038.jpg","description":"Moeka is a part-time editor whom Rintarou encounters in Akiba. She is practically glued to her phone and cannot function without it. Even when face to face, Moeka prefers to communicate by email.\nShe is searching for the legendary retro PC known as the IBN 5100.\n\n[From [url=http://steins-gate.us/characters.html]Steins;Gate Official Site[/url]]\n\n[Spoiler]Moeka was a constantly bullied social outcast; she desperately tried to make friends and find a place to belong, but failed miserably due to her social paranoia. In 2004, she fell into a deep depression due to hating everything and being hopeless, thinking that she no longer had a reason to live, Moeka decided to suicide. She drank several sleeping pills, hoping that she would die, but to no avail. As the pills were not working, her final attempt was to jump off a large building. At that moment, she received a mail from the enigmatic FB recruiting her to the Rounders, a SERN unit, changing her life forever. As FB often communicated with Moeka and acted as a mother figure towards her, speaking with Moeka about her problems and comforting her, she came to see FB as the person that gave her life meaning and devoted every waking moment to sending mails to them and carrying out their given orders, developing a dependency on FB and their mails. When that relationship which meant life to Moeka required her to kill someone, she killed. By taking another’s life, her life retained meaning. While Moeka is capable of carrying out atrocities for acceptance, she strives to be loved and has the potential to be a good person if led in the right direction.\n[/Spoiler]","spoil_gender":null,"aliases":"Shining Finger, Glowing Masseuse\nMail Demon\nMoe\nM4\nSHININGFINGER_MK","birthday":[6,6],"name":"Kiryuu Moeka","vns":[[2002,0,0,"primary"],[6618,0,0,"primary"],[9887,0,0,"side"],[11660,0,0,"primary"],[17102,0,0,"primary"],[21281,0,0,"appears"],[23221,0,0,"side"]]},
            |{"bloodt":"a","id":6497,"gender":"m","age":16,"aliases":"Urushibara Luka\nLukako\nRukako","birthday":[30,8],"name":"Urushibara Ruka","vns":[[2002,0,0,"primary"],[6618,0,0,"primary"],[9887,0,0,"primary"],[11660,0,0,"primary"],[17102,0,0,"side"],[21281,0,0,"appears"],[23221,0,0,"side"]],"image_flagging":{"violence_avg":0,"sexual_avg":0,"votecount":12},"original":"?? ??","image":"https://s2.vndb.org/ch/44/28044.jpg","spoil_gender":null,"description":"Ruka is a friend of [url=/c6498]Okabe[/url]. His appearance is that of a female and he acts in a feminine way due to his upbringing, wearing girl's clothing within and outside his father's temple. He is also a close friend and classmate of [url=/c6491]Mayuri's[/url] and is often asked to try on her cosplay costumes, but as he is quite shy, he generally refuses. He is unsure of his feelings towards Okabe, who calls him by his nickname, Rukako, containing the feminine suffix -ko, to denote his feminine appearance, and thinks of Ruka as his pupil."},
            |{"image":"https://s2.vndb.org/ch/29/10729.jpg","original":"?? ???","image_flagging":{"votecount":10,"violence_avg":0,"sexual_avg":0},"spoil_gender":null,"description":"Rintarou is an eccentric individual, a self-proclaimed mad scientist and often refers to himself under the alias of Hououin Kyouma, which is also the name he uses to introduce himself to other people. [url=/c6491]Mayuri[/url] and [url=/c6493]Daru[/url] refer to him by the nickname Okarin, a portmanteau of his surname and given name. He is the founder of what he calls the \"Future Gadget Laboratory\" in Akihabara where he spends most of his time. Rintarou gives off the appearance of being delusional and paranoid, frequently referring to the \"organization\" that is after him, talking to himself on his phone, and engaging in fits of maniacal laughter. Most of the time he takes on a fairly arrogant personality. He is usually seen wearing a lab coat. [spoiler]As he experiments with time travel, he learns that he is the only one who possesses the ability to determine changes between different timelines, which he dubs \"Reading Steiner\".[/spoiler] The character is 18 years old and a first year student at Tokyo Denki University.\n\nModified from [url=http://en.wikipedia.org/wiki/Steins;Gate]Wikipedia[/url].","aliases":"Okabe Rintaro\nHououin Kyouma\n?????\nOkarin\n????","birthday":[14,12],"name":"Okabe Rintarou","vns":[[2002,0,0,"main"],[6618,0,0,"main"],[9887,0,0,"main"],[11660,0,0,"primary"],[21281,0,0,"appears"],[23221,0,0,"main"]],"id":6498,"gender":"m","age":18,"bloodt":"a"},
            |{"vns":[[2002,0,0,"side"],[6618,0,0,"side"],[9887,0,0,"appears"],[11660,0,0,"primary"],[23221,0,0,"side"]],"name":"Tennouji Nae","aliases":"Sister Braun\nChipmunk","birthday":[9,11],"spoil_gender":null,"description":"Yuugo's daughter who lives with him in the TV repair store and gets along well with Shiina.","original":"??? ?","image":"https://s2.vndb.org/ch/29/97229.jpg","image_flagging":{"sexual_avg":0,"violence_avg":0,"votecount":11},"bloodt":"a","age":11,"id":15469,"gender":"f"},
            |{"name":"Akiha Yukitaka","vns":[[2002,0,0,"appears"],[11660,0,0,"side"]],"birthday":[null,null],"aliases":"Papa","spoil_gender":null,"description":"Father of [url=/c6494]Akiha Rumiho[/url] (a.k.a Faris NyanNyan). His family owns a large area in the downtown districts of Tokyo, hence \"Akihabara\".\n\n[spoiler]Yukitaka died in a plane crash several years before the events of Steins;Gate. He was the only victim of the crash. However, his daughter later uses a D-Mail to save his life, by making him believe she was kidnapped and forcing him to take the bullet train instead. However, due to the D-Mail, the Rounders were able to approach him and take his IBN 5100, which he had received from Amane Suzuha.\nHis survival led to a world line where Faris is a RaiNetter and Akihabara is still an Electric Town instead of a moe hub.[/spoiler]","image_flagging":{"votecount":11,"sexual_avg":0,"violence_avg":0},"image":"https://s2.vndb.org/ch/50/117950.jpg","original":"?? ??","bloodt":null,"age":null,"id":24992,"gender":"m"},
            |{"age":null,"gender":"m","id":24993,"bloodt":null,"description":"The story begins with Rintarou Okabe and Mayuri Shiina attending Nakabachi's press conference about time-travelling. His presentation angers Rintarou, who accuses him of plagiarizing John Titor's research. He loses his temper and yelled at Rintarou, ordering him to stop. He is not seen again after that.\n\n[spoiler]At the true end, Rintarou along with Suzuha Amane time travels back to the day of the conference to prevent Kurisu's death, it is revealed that his real name was Shouichi Makise, and that he was Kurisu's father. He attempted to murder her to steal her research on time-travelling. Rintarou tried to stop him, but unintentionally stabs Kurisu himself.\n\nThe research documents was later saved from a plane fire by Mayuri's metal Upa, which triggered the metal detector. Knowing this, Rintarou arranges so that the Upa put into the envelope by Kurisu is a plastic one. The metal detectors did not go off and the papers got destroyed by the fire, foiling Shouichi's plan.[/spoiler]","spoil_gender":null,"original":"?????","image":"https://s2.vndb.org/ch/71/30671.jpg","image_flagging":{"votecount":11,"sexual_avg":0,"violence_avg":0},"vns":[[2002,0,1,"appears"],[11660,0,0,"side"],[23221,0,0,"side"]],"name":"Makise Shouichi","birthday":[null,null],"aliases":"Doctor Nakabachi\nPapa"},
            |{"gender":"m","id":24994,"age":null,"bloodt":null,"image":"https://s2.vndb.org/ch/48/117948.jpg","original":"????","image_flagging":{"votecount":9,"sexual_avg":0,"violence_avg":0},"description":"Kouichi is a minor antagonist who first appeared in the Steins;Gate visual novel as the cheating opponent and sore loser to Faris in the Rai-Net Access Battlers Championship.","spoil_gender":null,"birthday":[null,null],"aliases":"4?\nShido","vns":[[2002,0,0,"appears"],[6618,0,0,"side"],[11660,0,0,"side"],[23221,0,0,"side"]],"name":"Suzuki Kouichi"},
            |{"description":"An unimportant character from a videogame in Steins;Gate. An ally of justice in Linear Bounded Phenogram.","spoil_gender":null,"image_flagging":{"violence_avg":0,"sexual_avg":0,"votecount":12},"image":"https://s2.vndb.org/ch/58/52558.jpg","original":"??????","vns":[[2002,0,0,"appears"],[11660,0,0,"side"]],"name":"Alpaca-man","aliases":"Alpaca commander","birthday":[null,null],"age":null,"id":43791,"gender":null,"bloodt":null},
            |{"gender":"f","id":73598,"age":null,"bloodt":"o","image_flagging":{"votecount":12,"violence_avg":0,"sexual_avg":0},"original":"??? ??","image":"https://s2.vndb.org/ch/93/97193.jpg","spoil_gender":null,"description":"Time traveller from 2036, daughter of Hashida Itaru with a mission to reach Steins Gate.","birthday":[27,9],"aliases":"Beta Suzuha","vns":[[2002,0,2,"appears"],[11660,0,1,"appears"],[17102,0,0,"primary"]],"name":"Amane Suzuha"}],"more":true,"num":15}""".trimMargin())
//        println(resp.json)

        val parse = Gson().fromJson<GetResults<Character>>(resp.json, object : TypeToken<GetResults<Character>>(){}.type)
//        assertFalse(parse.more)
    }
}