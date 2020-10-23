host="ecom.info"
echo "GET $host/customer_service/customer"
echo "";
GET $host/customer_service/customer | python -mjson.tool
echo ""; echo "#########################################################################"
echo "GET $host/product_service/product"
echo "";
GET $host/product_service/product | python -mjson.tool
echo ""