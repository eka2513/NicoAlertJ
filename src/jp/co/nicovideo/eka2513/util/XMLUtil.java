package jp.co.nicovideo.eka2513.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import jp.co.nicovideo.eka2513.constants.NicoAlertConstants;
import jp.co.nicovideo.eka2513.exception.UserException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUtil implements NicoAlertConstants {


	public Map<String, Object> parseAlertStatus(String xml) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
			        .newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
			XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            // 単一ノード取得
            String location = "/getalertstatus/user_hash/text()";
            result.put(USER_HASH, xpath.evaluate(location, doc));
            // 複数ノード取得
            location = "//communities/community_id/text()";
            NodeList entries = (NodeList) xpath.evaluate(
                                    location, doc, XPathConstants.NODESET );
            List<String> list = new ArrayList<String>();
            for( int i = 0; i < entries.getLength(); i++ ) {
            	list.add(entries.item(i).getNodeValue());
            }
            result.put(COMMUNITIES, list);
            location = "//ms/addr/text()";
            result.put(ADDR, xpath.evaluate(location, doc));
            location = "//ms/port/text()";
            result.put(PORT, xpath.evaluate(location, doc));
            location = "//ms/thread/text()";
            result.put(THREAD, xpath.evaluate(location, doc));
		} catch (ParserConfigurationException e) {
		} catch (UnsupportedEncodingException ignore) {
		} catch (SAXException e) {
			throw new UserException(e);
		} catch (IOException e) {
			throw new UserException(e);
		} catch (XPathExpressionException e) {
			throw new UserException(e);
		}
		return result;
	}

	public Map<String, String> parsePlayerStatus(String xml) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
			        .newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
			XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            // 単一ノード取得
            String location = "//ms/addr/text()";
            result.put(ADDR, xpath.evaluate(location, doc));
            location = "//ms/port/text()";
            result.put(PORT, xpath.evaluate(location, doc));
            location = "//ms/thread/text()";
            result.put(THREAD, xpath.evaluate(location, doc));
            location = "//stream/start_time";
            result.put(START_TIME, xpath.evaluate(location, doc));
            location = "//user/user_id";
            result.put(USER_ID, xpath.evaluate(location, doc));
            location = "//user/is_premium";
            result.put(PREMIUM, xpath.evaluate(location, doc));

		} catch (ParserConfigurationException e) {
		} catch (UnsupportedEncodingException ignore) {
		} catch (SAXException e) {
			throw new UserException(e);
		} catch (IOException e) {
			throw new UserException(e);
		} catch (XPathExpressionException e) {
			throw new UserException(e);
		}
		return result;
	}

}
