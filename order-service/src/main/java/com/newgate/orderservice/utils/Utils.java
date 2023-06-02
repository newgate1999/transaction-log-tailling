package com.newgate.orderservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
//import org.keycloak.KeycloakSecurityContext;
//import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
//import org.keycloak.representations.AccessToken;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class Utils {
  private Utils() {
    throw new IllegalStateException("Utils class");
  }

  public static String tryToWriteObjectAsJsonString(ObjectMapper objectMapper, Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
      throw new IllegalArgumentException("Could not write object as json string");
    }
  }

  /**
   * @author: vanna
   * @date: 13/01/2021
   * @note: append like expression
   */
  public static String appendLikeExpression(String value) {
    return String.format("%%%s%%", value);
  }

  /**
   * @author: vanna
   * @date: 13/01/2021
   * @note: get current user
   */
//  public static UserToken getUser() {
//    var securityContext = getKeycloakSecurityContext();
//    var token = securityContext.getToken();
//    var keycloakConfiguration = ApplicationContextHolder.getBean(KeycloakConfiguration.class);
//    AccessToken.Access resourceAccess = token.getResourceAccess(keycloakConfiguration.getResource());
//    List<String> clientRole = new ArrayList<>();
//    if(resourceAccess != null)
//      clientRole = new ArrayList<>(resourceAccess.getRoles());
//    if(token.getRealmAccess() != null) {
//      var realmRole = new ArrayList<>(token.getRealmAccess().getRoles());
//      clientRole.addAll(realmRole);
//    }
//    return UserToken.builder()
//        .username(token.getPreferredUsername())
//        .sessionState(token.getSessionState())
//        .token(securityContext.getTokenString())
//        .roles(clientRole.stream().distinct().collect(Collectors.toList()))
//        .build();
//  }
//
//  /**
//   * @author: vanna
//   * @date: 13/01/2021
//   * @note: get key cloak security context
//   */
//  private static KeycloakSecurityContext getKeycloakSecurityContext() {
//    var authentication = SecurityContextHolder.getContext().getAuthentication();
//    if (authentication == null) throw new NullPointerException();
//    if (!(authentication instanceof KeycloakAuthenticationToken))
//      throw new AccessDeniedException("Wrong authentication");
//    return ((KeycloakAuthenticationToken) authentication).getAccount().getKeycloakSecurityContext();
//  }

  /**
   * @author: vanna
   * @date: 13/01/2021
   * @note: list file import allow
   */
  public static List<String> fileExtensionAllow() {
    return Arrays.asList("xlsx", "csv", "xls");
  }

  /**
   * @author: vanna
   * @date: 13/01/2021
   * @note: check String is date?
   */
  public static boolean isValidDate(String inDate) {
    var simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    simpleDateFormat.setLenient(false);
    try {
      simpleDateFormat.parse(inDate.trim());
    } catch (ParseException pe) {
      return false;
    }
    return true;
  }


  /** @note: get date + day */
  public static Date addNDay(Integer day, Date date) {
    var cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, day);
    return cal.getTime();
  }

  /** @note: format date to String dd-MM */
  public static String formatDateToDDMM(Date date) {
    var calendar = Calendar.getInstance();
    calendar.setTime(date);
    return String.format(
        "%s-%s", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1);
  }

  public static String formatDateToDDMMYYYY(Date date) {
    var calendar = Calendar.getInstance();
    calendar.setTime(date);
    return String.format(
        "%s-%s-%s",
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.YEAR));
  }

  public static String formatDateToDDMMYYYYOther(Date date) {
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    return formatter.format(date);
  }

  public static Date date8hMorning() {
    var cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DAY_OF_WEEK, 1);
    cal.set(Calendar.HOUR_OF_DAY, 8);
    return cal.getTime();
  }

  /** @note: get days T+N */
  public static String days(Date date, Integer n) {
    List<String> lstDay = new ArrayList<>();
    lstDay.add(formatDateToDDMM(date));
    if (n >= 0) {
      for (var i = 1; i <= n; i++) {
        lstDay.add(formatDateToDDMM(addNDay(i, date)));
      }
    } else {
      for (var i = n; i < 0; i++) {
        lstDay.add(formatDateToDDMM(addNDay(i, date)));
      }
    }
    return lstDay.stream()
        .distinct()
        .map(s -> String.format("'%s'", s))
        .collect(Collectors.joining(","));
  }

  /** @note: get days T+N */
  public static List<String> daysBirthDay(Date date, Integer n) {
    List<String> lstDay = new ArrayList<>();
    lstDay.add(formatDateToDDMM(date));
    if (n >= 0) {
      for (var i = 1; i <= n; i++) {
        lstDay.add(formatDateToDDMM(addNDay(i, date)));
      }
    } else {
      for (var i = n; i < 0; i++) {
        lstDay.add(formatDateToDDMM(addNDay(i, date)));
      }
    }
    return lstDay;
  }

  public static Date addSecond(Date date, Integer second) {
    var cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.SECOND, second);
    return cal.getTime();
  }

  public static int getYear(Date date) {
    var cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.YEAR);
  }

  public static Date setHourMinute(Date date, Integer hour, Integer minute) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, 0);
    return calendar.getTime();
  }

  public static Date getCurrentDateWithOutTime(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  /* @note: get date + day */
  public static Date addNDay(Date date, Integer day) {
    var cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, day);
    return cal.getTime();
  }

  /**
   * @author: SonNd
   * @date: 19/08/2021
   * @note: Lấy ngày giờ phút theo cấu hình
   */
  public static Date dateBeforeHour(Date date, Integer hour, Integer minute) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    if (calendar.get(Calendar.HOUR_OF_DAY) < 8) {
      calendar.set(Calendar.HOUR_OF_DAY, 8);
      calendar.set(Calendar.MINUTE, 0);
    }
    calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 7);

    return calendar.getTime();
  }

  /**
   * @author: HuyenLTT
   * @date: 26/10/2021
   * @note: format string to date
   */
  public static Date formatStringToDate(String dateString, String format) throws ParseException {
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    return formatter.parse(dateString);
  }

  public static Map<String, String> getAllTagOfXml(Document doc, String nameRoot) {
    Map<String, String> allNodes = new HashMap<>();
    Node parentNode = doc.getElementsByTagName(nameRoot).item(0);
    NodeList childList = parentNode.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      if (childList.item(i) != null) {
        if ("c1".equals(childList.item(i).getNodeName()) || "c8".equals(childList.item(i).getNodeName()) || "c2".equals(childList.item(i).getNodeName())) {
          String nodeName = childList.item(i).getNodeName();
          String childNodeValue = childList.item(i).getTextContent();
          NamedNodeMap nodeAttrribute = childList.item(i).getAttributes();

          String nodeNameOfAtt = "";
          if (StringUtils.isNotEmpty(childNodeValue)) {
            if (nodeAttrribute.getNamedItem("m") != null) {
              nodeNameOfAtt = nodeAttrribute.getNamedItem("m").getNodeName() + nodeAttrribute.getNamedItem("m").getTextContent();
              allNodes.put(nodeName + "," + nodeNameOfAtt, childList.item(i).getTextContent());
            } else {
              allNodes.put(nodeName, childNodeValue);
            }
          }
        } else {
          allNodes.put(childList.item(i).getNodeName(), childList.item(i).getTextContent());
        }
      } else {
        log.error("getAllTagOfXml XML ERRROR {} {}", doc, nameRoot);
      }
    }
    return allNodes;
  }

  public static String formatDateToString(Date date, String format) {
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    return formatter.format(date);
  }
  public static Date addHour(Date date, Integer hour) {
    var cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.HOUR_OF_DAY, hour);
    return cal.getTime();
  }

  // Tiếng việt có dấu => không dấu
  public static String convertStringUTF8(String value) {
    try {
      var normalize = Normalizer.normalize(value, Normalizer.Form.NFD);
      return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalize).replaceAll("");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public static Date setHour(Date date, Integer hour) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.HOUR, hour);
    return calendar.getTime();
  }
}