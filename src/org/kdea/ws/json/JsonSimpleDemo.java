package org.kdea.ws.json;

import org.json.simple.*;

import org.json.simple.parser.*;



public class JsonSimpleDemo {

	public static void main(String[] args) {

		/* JSON ���ڿ��� �Ľ��Ͽ� JSONObject �� �ٷ�� �� */

		String jsonStr = 

		"{" +

			"\"name\":\"smith\", \"hobby\":[\"��ȭ\",\"����\",\"����\"]" +

		"}";

		JSONParser jsonParser = new JSONParser();

		try {

			JSONObject jsonObj = (JSONObject)jsonParser.parse(jsonStr);
//jsonobj �� map���� �Ǿ��ִ�
		
			String name = (String) jsonObj.get("name");

			System.out.println("�̸�:"+name);

			

			JSONArray jsonArr = (JSONArray) jsonObj.get("hobby");

			for(int i=0;i<jsonArr.size();i++){

				String hobby = (String) jsonArr.get(i);

				System.out.println(hobby);

			}

		} catch (ParseException e) {

			e.printStackTrace();

		}

		

		/* JSON ���ڿ��� �����ϴ� �� */

		JSONObject jsonObj = new JSONObject();
//���ڿ� ������ ��ó�� ���� �ȴ�.
		jsonObj.put("sender", "ȫ�浿");

		jsonObj.put("receiver", "Smith");

		jsonObj.put("msg", "�����մϴ�");

		String jsonSt = jsonObj.toJSONString();

		System.out.println(jsonSt);

	}

}