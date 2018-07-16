	
	//Firebase başlatılır.
  	var config = {
		apiKey: "AIzaSyC30urzySPG0rSwGrPgCPsG1S2UYG7CpWE",
		authDomain: "smart-mirror-4c629.firebaseapp.com",
		databaseURL: "https://smart-mirror-4c629.firebaseio.com",
		projectId: "smart-mirror-4c629",
		storageBucket: "smart-mirror-4c629.appspot.com",
		messagingSenderId: "705683740355"
  	};
  	firebase.initializeApp(config);
	
	/*HTML sayfasında bulunan; tarih, saat, başlık ve açıklama verilerine erişmek için ilgili kontrollere erişim sağlanır.*/
	var bday = document.getElementsByName("bday");
	var btime = document.getElementsByName("btime");
	var title = document.getElementsByName("title");
	var mainText = document.getElementsByName("note");
	
	/*Kayıt işlemini yapan butona erişim sağlanır.*/
	var submitBtn = document.getElementById("submitBtn");
	
	function saveData(){
		    /*Tarih bilgisi alınır değişkene atanır.*/
			var getDate=bday[0].valueAsDate;	
		
			/*Alınan tarih bilgisi dd.mm.yyyy formatı olarak yeniden düzenlenir ve yeni bir değişkene atanır.*/
			var date = getDate.getDate()+"."+(getDate.getMonth()+1)+"."+getDate.getFullYear();	
		
			/*Zaman bilgisi alınır ve değişkene atanır*/
			var getTime = btime[0].value;	
		
			/*Firebase ortamında veriler key-value olarak kayıt edilir. Value bilgisini kullanıcının girdiği tarih, saat, başlık ve açıklama verilerinden elde edeceğiz, key bilgisini ise o anki anlık zaman bilgisinden elde edeceğiz.*/
			var d = new Date();
			var n = d.getTime();

			/*Firebase ortamında veriyi kayıt edeceğimiz bir JSON dizisi tanımladık. Burada mynotes, dizinin ismi olacaktır*/
			var firebaseRef = firebase.database().ref("mynotes");
		
			/*Tarih, saat, başlık ve açıklama verileri arasına tire işareti ekledik ve yeni bir veri elde ettik. Burada elde edilen veri Firebase ortamına kayıt edeceğimiz veridir.*/
			var msg = date+"-"+getTime+"-"+title[0].value+"-"+mainText[0].value;
		
			/*Aşağıdaki satır ile n key (o anki zaman) bilgisine karşılık msg verisini kayıt ettik. Böylece kayıt işlemi tamamlanmış olur.*/
			firebaseRef.child(n).set(msg);

			/*Yeni kayıt için etiketler resetlenir.*/
			bday[0].value='';
			btime[0].value='';
			mainText[0].value='';
			title[0].value='';
	}

	/*table_body id bilgisine sahip olan etikette kayıtlı olan tüm veriler listelenir.
	Listeleme yapmadan önce aşağıdaki metot ile öncelikle bu etikette bulunan tüm verilerin silinmesi sağlanır */
	function removeAllChildsss(){
		
		/*table_body id bilgisine sahip etikete erişim sağlanır*/
		var list2 = document.getElementById("table_body");
		
		/*while döngüsü ile bu etikette herhangi bir verinin olup olmadığı kontrol edilir. Eğer varsa removeChild ile verinin silinmesi sağlanır.*/
		while (list2.hasChildNodes()) {
			list2.removeChild(list2.firstChild);
		}
	}

	/*Bu metot ile varsa kayıtlı verilerin listelenmesi sağlanır*/
	getList();

	/*Firebase ortamında kayıtlı olan tüm verilerin listelenmesini sağlayan metot.*/
	function getList(){
		
		/*Firebase veritabanında verilerimizi mynotes JSON tagı altında kayıt edeceğimiz için verilere erişmek için yine mynotes tagını kullandık. Bu şekilde varsa kayıtlı olan tüm verilerin list değişkenine atanması sağlanmış olur.*/
		var list = firebase.database().ref("mynotes");
		
		/*on metodu ile firebase ortamındaki verilen okunması hatta varsa veri değişikliklerinin algılanması sağlanır.*/
		list.on("child_added",snap => {	
			
			/*snap.val() ile elde edilen veriyi split ile parçaladık. Kayıt işleminde tarih, saat, başlık ve açıklama verileri arasına tire eklediğimiz için veriyi parçalarken tire karakterini kullandık. Böylece elimizde bir dize oluşur.*/
			var res = snap.val().split("-");
			var i;
			var r = document.createElement("TR");
			
			/*Kayıtlı olan veriler listelenir.*/
			for (i = 0; i < res.length; i++) {
				var t = document.createElement("TD");
				var text = document.createTextNode(res[i]);
				r.appendChild(t);
				t.appendChild(text);
			}			
			
			/*Veriler listelendikten hemen sonra bir tane buton ekledik. Bu buton ilgili kaydın silinmesini sağlayacaktır.*/
			var t2 = document.createElement("TD");
			var b = document.createElement("BUTTON");
			var bText = document.createTextNode("Yes");
			r.appendChild(t2);
			t2.appendChild(b);
			b.appendChild(bText);

			/*Butona tıklama olayı tanımladık. Tıklandıktan sonra snap.key ile bilgisi verilen veri Firebase ortamından silinir.*/
			var att = document.createAttribute("onclick");
			att.value = "removeAct("+snap.key+")";
			b.setAttributeNode(att);

			document.getElementById("table_body").appendChild(r);

		});
	}
	
	/*key bilgisi verilen veriyi silen metodumuz*/
	function removeAct(x) {
    	var firebaseRef = firebase.database().ref("mynotes");
		firebaseRef.child(x).remove();
		removeAllChildsss();
		getList();
		
	}
	
	