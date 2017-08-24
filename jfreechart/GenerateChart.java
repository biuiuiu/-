package com.fiberhome.ms.cus.dayreport.Utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class GenerateChart {
	/**
	 * author zya
	 */

	/*
	 * 
	 * 生成餅狀圖，表頭和數據源
	 * 
	 * */
	public static void generatePieChart(Map<String, String> map , String title,String filePath , String fileName) throws IOException {
		
		DefaultPieDataset dataset = new DefaultPieDataset( );
		for (String item : map.keySet()) {
			String temp = map.get(item);
			if (temp != null && temp.contains("%")) {
				temp = temp.replace("%", "");
				map.put(item, temp);
			}
			if (temp == null || temp.equals("") || temp.equals("-")) {
				map.put(item, "0");
			}
			String valueTemp = item.concat(" "+map.get(item)).concat("%");
			dataset.setValue(valueTemp, Float.parseFloat(map.get(item)));
		}
		ChartUtils.setChartTheme();
		JFreeChart chart = ChartFactory.createPieChart(
		   title, // chart title
		   dataset, // data
		   true, // include legend
		   true,
		   false);
		   
		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */ 
		String newFilePath = filePath + File.separator + fileName + ".jpeg";
		File pieChart = new File(newFilePath); 
		ChartUtilities.saveChartAsJPEG( pieChart , chart , width , height );
		}
	/*
	 * 
	 * 生成柱狀圖，數據源和表頭和xy軸名稱
	 * 
	 * */
	public static void  generateBarChart(Map<String, String> map , String title , String x ,String y,String filePath,String fileName) throws IOException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		for (String item : map.keySet()) {
			String temp = map.get(item);
			if (temp != null && temp.contains("%")) {
				temp.replace("%", "");
				map.put(item, temp);
			}
			if (temp != null && temp.equals("-")) {
				map.put(item, null);
			}
			Number number = map.get(item)==null?null:Float.parseFloat(map.get(item));
			dataset.setValue(number, "", item);
		}
		JFreeChart barChart = ChartFactory.createBarChart(
		         title, 
		         x, y, 
		         dataset,PlotOrientation.VERTICAL, 
		         true, true, false);
		 ChartUtils.setChartTheme();
		 ChartUtils.setAntiAlias(barChart);    
	      int width = 800; /* Width of the image */
	      int height = 480; /* Height of the image */ 
	      String newFilePath = filePath + File.separator + fileName + ".jpeg";
	      File BarChart = new File(newFilePath); 
	      ChartUtilities.saveChartAsJPEG( BarChart , barChart , width , height );
	}
	public static void main(String[] args) throws IOException {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("Aaaaa", "22");
		map.put("BBBB","50");
		map.put("CCCC", null);
		map.put("dddd", "12");
		generatePieChart(map, "test", FileName.IMAGE_FILE_PATH, "1646135161");
	}
	/*
	 * 
	 * 生成柱狀圖list，數據源和表頭和xy軸名稱
	 * 數據源是list<Map<String,Number>>
	 * */
	public static void  generateBarChartByList(List<Map<String, String>> list, String title , String x ,String y,String filePath,String fileName) throws IOException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		for (Map<String, String> map : list) {
			for (String item : map.keySet()) {
				String temp = map.get(item);
				if (temp != null && temp.contains("%")) {
					String ss = temp.replace("%", "");
					map.put(item, ss);
				}
				if (temp != null && temp.equals("-")) {
					map.put(item, null);
				}
				if (!item.equals("年份")) {
					Number number = map.get(item)==null?null:Float.parseFloat(map.get(item));
					dataset.setValue(number, map.get("年份"), item);
				}
			}
		}
		 JFreeChart barChart = ChartFactory.createBarChart(
		         title, 
		         x, y, 
		         dataset,PlotOrientation.VERTICAL, 
		         true, true, false);
		  ChartUtils.setChartTheme();
		  ChartUtils.setAntiAlias(barChart);
		  ChartUtilities.applyCurrentTheme(barChart);
	      int width = 800; /* Width of the image */
	      int height = 480; /* Height of the image */ 
	      String newFilePath = filePath + File.separator + fileName + ".jpeg";
	      File BarChart = new File(newFilePath); 
	      ChartUtilities.saveChartAsJPEG( BarChart , barChart , width , height );
	}
	}
	
	
