package com.wes.inspection.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wes.inspection.entity.PageBean;
import com.wes.inspection.entity.Product;
import com.wes.inspection.service.ProductService;
import com.wes.inspection.util.ResponseUtil;
import com.wes.inspection.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
* @author: xiaoxinquan
* @date：2018年11月20日 下午3:02:39 
* 类说明 
**/

@Controller
@RequestMapping(value="/basic")
public class ProductController {


	@Autowired
	private ProductService productService;
	
	@RequestMapping("/getProduct")
	public String getProduct(int id,Model model){
		model.addAttribute("product",productService.findProductById(id));
		return "editProduct";
	}
	
	   /**
     * 查询所有产品
     * @param request
     * @param model
     * @return
     */
//    @RequestMapping("/list.do")  
//    public String getAllProduct(Model model){
//        List<Product> product = productService.findAll();
//		////JSONObject json = JSONObject.fromObject(product);
//        //odel.addAttribute("userList",product);
//		JSONArray json = JSONArray.fromObject(product);
//		String s = "{\"rows\":" + json + "}";
//		//this.outJson(s);
//		//this.out
//        return s.toString();
//    }
//    
    
    @RequestMapping("/list")
    public String list(@RequestParam(value = "page", required = false) String page, @RequestParam(value = "rows", required = false) String rows,Product product, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        if (page != null && rows != null) {
            PageBean pageBean = new PageBean(Integer.parseInt(page),
                    Integer.parseInt(rows));
            map.put("start", pageBean.getStart());
            map.put("size", pageBean.getPageSize());
        }
        map.put("productLine", StringUtil.formatLike(product.getProductLine()));
        List<Product> productList = productService.findProduct(map);
        Long total = productService.getTotalProduct(map);
        JSONObject result = new JSONObject();
        JSONArray jsonArray = JSONArray.fromObject(productList);
        result.put("rows", jsonArray);
        result.put("total", total);
        System.out.println("request: user/list , map: " + map.toString());
        ResponseUtil.write(response, result);
        return null;
    }
    
    @RequestMapping("/delete")
    public String delete(@RequestParam(value = "ids") String ids, HttpServletResponse response) throws Exception {
        JSONObject result = new JSONObject();
        String[] idsStr = ids.split(",");
        for (int i = 0; i < idsStr.length; i++) {
        	productService.deleteProduct(Integer.parseInt(idsStr[i]));
        }
        result.put("success", true);
        //log.info("request: user/delete , ids: " + id);
        ResponseUtil.write(response, result);
        return null;
    }
    
    @RequestMapping("/save")
    public String save(Product product, HttpServletResponse response) throws Exception {
        int resultTotal = 0;
        //String MD5pwd = MD5Util.MD5Encode(user.getPassword(), "UTF-8");
        //user.setPassword(MD5pwd);
        if (product.getId() == null) {
            resultTotal = productService.addProduct(product);
        } else {
            resultTotal = productService.updateProduct(product);
        }
        JSONObject result = new JSONObject();
        if (resultTotal > 0) {
            result.put("success", true);
        } else {
            result.put("success", false);
        }
        //log.info("request: user/save , user: " + user.toString());
        ResponseUtil.write(response, result);
        return null;
    }

	
}
