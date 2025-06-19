from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


driver = webdriver.Firefox()
driver.get("https://www.kaufland.hr/akcije-novosti/popis-mpc.html")
cookie_button = driver.find_element(By.ID, "onetrust-accept-btn-handler")
cookie_button.click()
WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.PARTIAL_LINK_TEXT, ".csv")))

links = driver.find_elements(By.PARTIAL_LINK_TEXT, ".csv")

kaufland_addr = "https://www.kaufland.hr"

with open("G:\Dev\Prices\links\kaufland.csv", "w", encoding="utf-8") as file:

    for link in links:
        file.write(link.accessible_name[1:] + "," + kaufland_addr + link.get_dom_attribute("href") + "\n")
       
driver.quit()
