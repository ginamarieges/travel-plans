package com.ginamarieges.travelplans.web;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Ensures all requests and responses use UTF-8 encoding.
 * This filter prevents character encoding issues with special characters
 * like accents (á, é, í, ó, ú), ñ, and other non-ASCII characters.
 * 
 * Applied to all URLs ("/*") to guarantee consistent encoding throughout
 * the application, fixing issues like "Plan de 4 días" displaying as "Plan de 4 dÃÂ­as".
 */
@WebFilter("/*")
public class CharacterEncodingFilter implements Filter {
    
    private static final String UTF_8 = "UTF-8";
    
    /**
     * Initializes the filter. No special configuration needed.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization required
    }
    
    /**
     * Sets UTF-8 encoding for both request and response before processing.
     * This ensures form data is correctly interpreted and responses are properly encoded.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // Set UTF-8 for incoming request data (form submissions, query params)
        request.setCharacterEncoding(UTF_8);
        
        // Set UTF-8 for outgoing response data (HTML, JSON, etc.)
        response.setCharacterEncoding(UTF_8);
        
        // Continue with the request processing
        chain.doFilter(request, response);
    }
    
    /**
     * Cleanup method called when filter is destroyed. No resources to release.
     */
    @Override
    public void destroy() {
    }
}
