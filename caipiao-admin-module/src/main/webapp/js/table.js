 $(document).ready(function () {
     $('#myTable01').fixedHeaderTable({footer: true, cloneHeadToFoot: true, altClass: 'odd', autoShow: false});

     $('#myTable01').fixedHeaderTable('show', 1000);

     $('#myTable02').fixedHeaderTable({footer: true, altClass: 'odd'});

     $('#myTable05').fixedHeaderTable({altClass: 'odd', footer: true, fixedColumns: 1});

     $('#myTable03').fixedHeaderTable({altClass: 'odd', footer: false, fixedColumns: 2});
     $('#contractTable01').fixedHeaderTable({altClass: 'odd', footer: true, fixedColumns: 3});

     $('#myTable04').fixedHeaderTable({altClass: 'odd', footer: true, cloneHeadToFoot: true, fixedColumns: 3});
 });

$(function () {
    function Tables($n) {
        this._html = $n.clone();
        this._sourceNode = $n;
        this.init = function () {
            this._sourceNode.html($(this._html).html());
        }
    }

    window.FixedTables = function ($n) {
        return new Tables($n);
    }
})
