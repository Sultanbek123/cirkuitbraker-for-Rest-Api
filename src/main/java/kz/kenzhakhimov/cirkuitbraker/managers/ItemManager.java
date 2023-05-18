package kz.kenzhakhimov.cirkuitbraker.managers;

import kz.kenzhakhimov.cirkuitbraker.dto.ItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


@Component
public class ItemManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemManager.class);

    @Autowired
    private RestTemplate restTemplate;

    public List<ItemDto> getAllItemsFromLibrary ()
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<List<ItemDto>> responseEntity;
        long startTime = System.currentTimeMillis();
        LOGGER.info("Start time = {}", startTime);
        try
        {
            responseEntity= restTemplate.exchange(buildUrl(),
                    HttpMethod.GET, null, new ParameterizedTypeReference<List<ItemDto>>()
                    {});
            if(responseEntity != null && responseEntity.hasBody())
            {
                Thread.sleep(3000);
                LOGGER.info("Total time to retrieve results = {}",
                        System.currentTimeMillis() - startTime);
                return responseEntity.getBody();
            }
        }
        catch (URISyntaxException e)
        {
            LOGGER.error("URI has a wrong syntax", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("No result found, returning an empty list");
        return new ArrayList<>();
    }

    private URI buildUrl () throws URISyntaxException
    {
        return new URI("http://localhost:8080/api");
    }


}
