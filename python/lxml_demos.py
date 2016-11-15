# -*- coding: utf-8 -*-
# Created by 'shgy' on '15-8-19'
from unittest import TestCase


class lxmltest(TestCase):

    def test_fun_strip_tags(self):
        """
        use etree.strip_tags() function
        delete all sub elements of td,
        只有一层，而不会递归处理
        """
        from lxml import etree
        with open("test-data/test.html") as f:
            html = f.read()
            tree = etree.HTML(html)
            map(lambda x: etree.strip_tags(x, "*"), tree.xpath(".//td"))
            print etree.tostring(tree,method='html')

    def test_fun_get_children(self):
        """
        _Element.getchildren(self)
        Returns all direct children. The elements are returned in document order.
        """
        html_src = """
        <html>
            <body>
                <table>
                    <tr>
                        <td>abc</td>
                    </tr>
                    <tr>
                        <td>def</td>
                    </tr>
                </table>
            </body>
        </html>

        """
        from lxml import etree
        tree = etree.HTML(html_src)
        tr = tree.find('.//tr')
        children = tr.getchildren()
        print etree.tostring(children, method='html')
        print '================='

    def test_following_sibling(self):
        """
        xpath: following-sibling can only use in xpath function
        Returns all direct children. The elements are returned in document order.
        """
        html_src = """
        <html>
            <body>
                <table>
                    <tr>
                        <td>abc</td>
                    </tr>
                    <tr>
                        <td>def</td>
                    </tr>
                </table>
                <div>
                    <table>
                    <tr>
                        <td>ghi</td>
                    </tr>
                    <tr>
                        <td>opq</td>
                    </tr>
                    </table>
                </div>
                <p>abcd</p>
            </body>
        </html>

        """
        from lxml import etree
        tree = etree.HTML(html_src)
        tables = tree.xpath('.//table/following-sibling::table[1]|.//table/following-sibling::div/table[1]')
        print etree.tostring(tables[0], method='html')
        print '================='


if __name__ == '__main__':
    pass