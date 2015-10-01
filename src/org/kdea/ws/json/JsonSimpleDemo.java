package org.kdea.ws.json;

import org.json.simple.*;

import org.json.simple.parser.*;



public class JsonSimpleDemo {

	public static void main(String[] args) {

		/* JSON 문자열을 파싱하여 JSONObject 로 다루는 예 */

		String jsonStr = 

		"{" +

			"\"name\":\"smith\", \"hobby\":[\"영화\",\"게임\",\"독서\"]" +

		"}";

		JSONParser jsonParser = new JSONParser();

		try {

			JSONObject jsonObj = (JSONObject)jsonParser.parse(jsonStr);
//jsonobj 는 map으로 되어있다
		
			String name = (String) jsonObj.get("name");

			System.out.println("이름:"+name);

			

			JSONArray jsonArr = (JSONArray) jsonObj.get("hobby");

			for(int i=0;i<jsonArr.size();i++){

				String hobby = (String) jsonArr.get(i);

				System.out.println(hobby);

			}

		} catch (ParseException e) {

			e.printStackTrace();

		}

		

		/* JSON 문자열을 생성하는 예 */

		JSONObject jsonObj = new JSONObject();
//문자열 넣을때 맵처럼 쓰면 된다.
		jsonObj.put("sender", "홍길동");

		jsonObj.put("receiver", "Smith");

		jsonObj.put("msg", "감사합니다");

		String jsonSt = jsonObj.toJSONString();

		System.out.println(jsonSt);

	}

}