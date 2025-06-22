<script>
        let stu = {
            name:"byterain",
            age:22,
            phone:'13388990099',
            getNameLength:function(){
               return this.name.length``
            },
            address:{
                code:'030006',
                info:'上海市静安区南京西路1号'
            },
            language:["Java","C","C++"]
        };
        console.log(stu.name);
        console.log(stu.getNameLength());
        console.log(stu.address.info);
        console.log(stu.language)
</script>